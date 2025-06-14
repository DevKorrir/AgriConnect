package dev.korryr.agrimarket.ui.features.posts.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import dev.korryr.agrimarket.ui.features.posts.dataModel.repo.FarmPostServiceImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

@HiltViewModel
class FarmPostViewModel @Inject constructor(
    private val repository: FarmPostServiceImpl,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _recentPosts = MutableStateFlow<List<FarmPost>>(emptyList())
    val recentPosts: StateFlow<List<FarmPost>> = _recentPosts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isPostSuccessful = MutableStateFlow(false)
    val isPostSuccessful: StateFlow<Boolean> = _isPostSuccessful.asStateFlow()

    /**
     * Creates a new farm post.
     *
     * @param imageUrl
     *   This should be the **local** (device) URI string, e.g. "content://com.android.providers.media.documents/document/image%3A2297839".
     *   Internally, we will:
     *     1) Parse it back to Uri
     *     2) Upload that Uri to Firebase Storage under "posts_images/..."
     *     3) Await the HTTPS download URL
     *     4) Build a FarmPost with that download URL
     *     5) Call repository.createPost(...) to save to Firestore
     *
     * @param description  Product description
     * @param price        Price as a Double
     * @param quantity     Quantity as Int
     * @param size         Farm size description (e.g. "2 acres")
     */

    fun createPost(
        imageUrl: String,
        description: String,
        price: Double,
        quantity: Int,
        size: String
    ){
        viewModelScope.launch {

            _isPosting.value = true

            try{

                _error.value = null
                _isPostSuccessful.value = false

                // 1. Get current user UID (farmId)
                val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                // 1a. Fetch that user’s FarmProfile from Firestore
                val profileDoc = firestore
                    .collection("farms")
                    .document(uid)
                    .get()
                    .await()

                val farmType = profileDoc.getString("typeOfFarming") ?: "Unknown"

                // 2. Parse the local URI string back to Uri
                val localUri: Uri = imageUrl.toUri()

                // 3. Upload that Uri to Firebase Storage under "posts_images/{randomUuid}.jpg"
                val randomFilename = UUID.randomUUID().toString() + ".jpg"
                val storageRef = storage.reference.child("posts_images/$randomFilename")

                // putFile(...) will start the upload; await() suspends until done
                storageRef.putFile(localUri).await()

                // 4. Once upload succeeds, fetch the HTTPS download URL
                val downloadUrl = storageRef.downloadUrl.await()

                val post = FarmPost(
                    farmId = uid,
                    imageUrl = downloadUrl.toString(),
                    description = description,
                    price = price,
                    quantity = quantity,
                    size = size,
                    timestamp  = System.currentTimeMillis(),
                    type = farmType
                )

                // 6. Delegate to your repository to write this FarmPost into Firestore
                //    repository.createPost(post) returns Result<String>, where String is the new postId
                val result = repository.createPost(post) //results is now Result<String>

                if (result.isSuccess) {
                    // result.getOrNull() would give you the String ID if you need it here
                    // Log.d("FarmPostViewModel", "Post created successfully with ID: $postId")

                    // 6a. If writing to Firestore succeeded, immediately reload recent posts for this farmer
                    loadRecentPosts(uid) // Assuming uid is available here

                    // 6b. Notify UI that the post was successful
                    _isPostSuccessful.value = true
                    _error.value = null
                } else {
                    //some error occur in the repo
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to create post"
                    _isPostSuccessful.value = false
                }
            } catch (e: Exception){
                //any exeception during upload
                _error.value = e.localizedMessage
                _isPostSuccessful.value = false

            } finally {
                _isPosting.value = false

            }
        }
    }

    /**
     * Loads the most recent posts for the given farmId (farmer UID) from the repository.
     * You can call this after a successful create, or from your UI when you first need to display them.
     */
    fun loadRecentPosts(farmId: String) {
        viewModelScope.launch {
            try {
                _recentPosts.value = repository.getRecentPosts(farmId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    /**
     * Call this after the UI has observed a successful post, so that the “success” state is reset.
     */
    fun resetPostSuccessState() {
        _isPostSuccessful.value = false
    }

    /**
     * Clear any existing error message.
     */
    fun clearError() {
        _error.value = null
    }














}