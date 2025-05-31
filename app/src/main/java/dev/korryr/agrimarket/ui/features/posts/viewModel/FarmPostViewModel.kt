package dev.korryr.agrimarket.ui.features.posts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import dev.korryr.agrimarket.ui.features.posts.dataModel.repo.FarmPostRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmPostViewModel @Inject constructor(
    private val repository: FarmPostRepositoryImpl,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _recentPosts = MutableStateFlow<List<FarmPost>>(emptyList())
    val recentPosts: StateFlow<List<FarmPost>> = _recentPosts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isPostSuccessful = MutableStateFlow(false)
    val isPostSuccessful: StateFlow<Boolean> = _isPostSuccessful.asStateFlow()

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

                val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                val post = FarmPost(
                    farmId = uid,
                    imageUrl = imageUrl,
                    description = description,
                    price = price,
                    quantity = quantity,
                    size = size
                )
                val result = repository.createPost(post) //results is now Result<String>

                if (result.isSuccess) {
                    // result.getOrNull() would give you the String ID if you need it here
                    val postId = result.getOrNull()
                    // Log.d("FarmPostViewModel", "Post created successfully with ID: $postId")
                    loadRecentPosts(uid) // Assuming uid is available here
                    _isPostSuccessful.value = true
                    _error.value = null
                } else {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to create post"
                    _isPostSuccessful.value = false
                }
            } catch (e: Exception){
                _error.value = e.localizedMessage
                _isPostSuccessful.value = false

            } finally {
                _isPosting.value = false

            }
        }
    }

    private fun loadRecentPosts(farmId: String) {
        viewModelScope.launch {
            try {
                _recentPosts.value = repository.getRecentPosts(farmId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun resetPostSuccessState() {
        _isPostSuccessful.value = false
    }

    // Optional: Add a method to clear error state
    fun clearError() {
        _error.value = null
    }














}