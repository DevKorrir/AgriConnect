package dev.korryr.agrimarket.ui.features.market.dataModel.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    /**
     * Reads all documents from Firestore “farm_posts” collection,
     * maps each document to FarmPost data class.
     */
    suspend fun getAllFarmPosts(): List<FarmPost> {
        return try {
            firestore.collection("farm_posts")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    // Each doc should have fields: imageUrl, description, price, quantity, unit, type, farmerId, etc.
                    doc.toObject(FarmPost::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }


    // 1) Stream all posts in real time as a StateFlow
    fun streamAllPosts(): Flow<List<FarmPost>> = callbackFlow {
        val subscription = firestore
            .collection("farm_posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)  // close the flow on error
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(FarmPost::class.java)?.copy(postId = doc.id)
                    } ?: emptyList()
                trySend(posts)
            }
        awaitClose { subscription.remove() }
    }

    // 2) Stream a single FarmProfile
    fun streamFarmProfile(farmId: String): Flow<FarmProfile?> = callbackFlow {
        val subscription = firestore
            .collection("farms")
            .document(farmId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val profile = snapshot?.toObject(FarmProfile::class.java)
                trySend(profile)
            }
        awaitClose { subscription.remove() }
    }

    // 3) Stream like count for a post
    fun streamLikeCount(postId: String): Flow<Int> = callbackFlow {
        val subscription = firestore
            .collection("farm_posts")
            .document(postId)
            .collection("likes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { subscription.remove() }
    }

    // 4) Stream “did current user like this post?”
    fun streamUserLiked(postId: String): Flow<Boolean> = callbackFlow {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUser == null) {
            trySend(false)
            close()
            return@callbackFlow
        }
        val subscription = firestore
            .collection("farm_posts")
            .document(postId)
            .collection("likes")
            .document(currentUser)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.exists() == true)
            }
        awaitClose { subscription.remove() }
    }

    // 5) Stream comment count
    fun streamCommentCount(postId: String): Flow<Int> = callbackFlow {
        val subscription = firestore
            .collection("farm_posts")
            .document(postId)
            .collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { subscription.remove() }
    }

    // 6) Stream bookmark state
    fun streamBookmarked(postId: String): Flow<Boolean> = callbackFlow {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUser == null) {
            trySend(false)
            close()
            return@callbackFlow
        }
        val subscription = firestore
            .collection("users")
            .document(currentUser)
            .collection("bookmarks")
            .document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.exists() == true)
            }
        awaitClose { subscription.remove() }
    }

    // 7) Stream follow state for a farm
    fun streamUserFollows(farmId: String): Flow<Boolean> = callbackFlow {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUser == null) {
            trySend(false)
            close()
            return@callbackFlow
        }
        val subscription = firestore
            .collection("users")
            .document(currentUser)
            .collection("following")
            .document(farmId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.exists() == true)
            }
        awaitClose { subscription.remove() }
    }

    // Toggle functions remain the same as before:
    fun toggleLike(postId: String) { /* … */ }
    fun toggleBookMark(postId: String) { /* … */ }
    fun toggleFollow(farmId: String) { /* … */ }





}
