package dev.korryr.agrimarket.ui.features.posts.dataModel.repo

//import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // check it was just adding
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

    override suspend fun getRecentPosts(farmId: String): Flow<List<FarmPost>> = callbackFlow {
        val listener = postsCollection
            .whereEqualTo("farmId", farmId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)  // terminates the flow with an exception
                } else {
                    val posts = snapshot?.toObjects(FarmPost::class.java) ?: emptyList()
                    trySend(posts).isSuccess  // safely enqueue the list
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getAllPostsForFarmer(farmId: String): Flow<List<FarmPost>> = callbackFlow {
        // Build the query: all posts for this farm, ordered newest→oldest
        val query = postsCollection
            .whereEqualTo("farmId", farmId)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        // Attach a snapshot listener
        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Terminate the flow with the error
                close(error)
            } else {
                // Convert every document to FarmPost (including its ID)
                val postsList = snapshot
                    ?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(FarmPost::class.java)
                            ?.copy(postId = doc.id)
                    }
                    ?: emptyList()

                // Emit the full list
                trySend(postsList).isSuccess
            }
        }

        // Clean up when nobody is collecting
        awaitClose { listenerRegistration.remove() }
    }



}
