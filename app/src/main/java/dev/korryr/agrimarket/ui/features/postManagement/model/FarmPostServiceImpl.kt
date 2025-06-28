package dev.korryr.agrimarket.ui.features.postManagement.model

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
class FarmPostServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage

) : FarmPostsService {

    private val postsCollection = firestore.collection("farm_posts")
    private val storageRef = storage.reference.child("post_images")

    override fun getAllPostsForFarmer(farmerId: String): Flow<List<FarmPost>> = callbackFlow {
        val listener = postsCollection
            .whereEqualTo("farmId", farmerId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

//                val posts = snapshot?.toObjects<FarmPost>() ?: emptyList()
//                trySend(posts)
                val posts = snapshot?.toObjects<FarmPost>() ?: emptyList()
                trySend(posts)

            }
        awaitClose { listener.remove()}
    }

    override suspend fun updatePost(post: FarmPost): Result<Unit> {
        return try {
            postsCollection.document(post.postId)
                .set(post)
                .await()
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            // Get the post first to get the image URL
            val postDoc = postsCollection.document(postId).get().await()
            val post = postDoc.toObject(FarmPost::class.java)

            // Delete the post document
            postsCollection.document(postId).delete().await()

            // Delete the associated image if it exists
            post?.imageUrl?.let { imageUrl ->
                if (imageUrl.isNotBlank()) {
                    deleteImage(imageUrl)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(imageUri: String): Result<String> {
        return try {
            val imageRef = storageRef.child("${UUID.randomUUID()}.jpg")
            val uploadTask = imageRef.putFile(imageUri.toUri()).await()
            val downLoadUjrl = uploadTask.storage.downloadUrl.await()
            Result.success(downLoadUjrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val imageRef = storage.getReferenceFromUrl(imageUrl)
            imageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}