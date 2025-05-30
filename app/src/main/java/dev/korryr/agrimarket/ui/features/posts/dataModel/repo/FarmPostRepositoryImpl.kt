package dev.korryr.agrimarket.ui.features.posts.dataModel.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FarmPostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmPostRepository {

    private val postsCollection = firestore.collection("farm_posts")

    override suspend fun createPost(post: FarmPost): String {
        val newDoc = postsCollection.document()
        val postWithId = post.copy(postId = newDoc.id)
        newDoc.set(postWithId).await()
        return newDoc.id
    }

    override suspend fun getRecentPosts(farmId: String): List<FarmPost> {
        return postsCollection.whereEqualTo("farmId", farmId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .await()
            .toObjects(FarmPost::class.java)
    }
}
