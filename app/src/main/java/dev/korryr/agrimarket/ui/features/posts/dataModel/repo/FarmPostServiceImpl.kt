package dev.korryr.agrimarket.ui.features.posts.dataModel.repo

import com.google.firebase.firestore.FirebaseFirestore
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FarmPostServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmPostService {

    private val postsCollection = firestore.collection("farm_posts")

    override suspend fun createPost(post: FarmPost): Result<String> {
        return try {
        val newDoc = postsCollection.document()
        val postWithId = post.copy(postId = newDoc.id)
        newDoc.set(postWithId).await()
        Result.success(newDoc.id)  // Return success with the document ID
    } catch (e: Exception) {
            // You might want to log the exception here or handle specific Firebase exceptions
        Result.failure(e)
    }
    }

    override suspend fun getRecentPosts(farmId: String): List<FarmPost> {
        // This function can also be modified to return Result<List<FarmPost>>
        // if you want consistent error handling, but for now, we'll leave it
        // as you might handle its errors differently.
        return try {
         postsCollection.whereEqualTo("farmId", farmId)
            //.orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .await()
            .toObjects(FarmPost::class.java)
            } catch (e: Exception) {
            // Handle error, e.g., return empty list or rethrow as a custom exception
            // For now, let's return an empty list on error for this example.
            // In a real app, you'd likely want to propagate the error.
            emptyList()
        }
    }





}
