package dev.korryr.agrimarket.ui.features.market.dataModel.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
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

    // 2) Stream all farm profiles
    fun streamAllFarmProfiles(): Flow<List<FarmProfile>> = callbackFlow {
        val subscription = firestore
            .collection("farms")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val farms = snapshot?.documents
                    ?.mapNotNull { it.toObject(FarmProfile::class.java)?.copy(farmId = it.id) }
                    ?: emptyList()
                trySend(farms)
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
        val currentUser = auth.currentUser?.uid
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

    /** 8) Stream “follower count” for a given farm (if you store follower lists) */
    fun streamFollowerCount(farmId: String): Flow<Int> = callbackFlow {
        // Example: if you store each user’s “following” array, you can query “whereArrayContains”
        val subscription = firestore
            .collection("farms").document(farmId)
            .collection("followers")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { subscription.remove() }
    }

    /** 9) Toggle “like” for a post (non‐Composable) */
    suspend fun toggleLike(postId: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val likeRef = firestore
            .collection("farm_posts").document(postId)
            .collection("likes").document(userId)

        val snap = likeRef.get().await()
        return if (snap.exists()) {
            likeRef.delete().await()
            false
        } else {
            likeRef.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()
            true
        }
    }


    /** 10) Toggle “bookmark” for a post */
    fun toggleBookMark(postId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val bookmarkDoc = firestore
            .collection("users")
            .document(currentUser)
            .collection("bookmarks")
            .document(postId)

        bookmarkDoc.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                bookmarkDoc.delete()
            } else {
                bookmarkDoc.set(mapOf("timestamp" to FieldValue.serverTimestamp()))
            }
        }
    }

    private val usersCollection = firestore.collection("users")
    private val farmsCollection = firestore.collection("farms")

    /** 11) Toggle “follow” for a farm */
    suspend fun toggleFollow(farmId: String): Boolean {
        val currentUser = auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in")

        // References for the two “links”
        val userFollowRef = usersCollection
            .document(currentUser)
            .collection("following")
            .document(farmId)

        val farmFollowerRef = farmsCollection
            .document(farmId)
            .collection("followers")
            .document(currentUser)

        // Read current state
        val snap = userFollowRef.get().await()
        return if (snap.exists()) {
            // Unfollow path
            userFollowRef.delete().await()
            farmFollowerRef.delete().await()
            false
        } else {
            // Follow path
            val data = mapOf("timestamp" to FieldValue.serverTimestamp())
            userFollowRef.set(data).await()
            farmFollowerRef.set(data).await()
            true
        }
    }

    /**
     * Check whether the current user already follows this farm.
     */
    suspend fun isFollowing(farmId: String): Boolean {
        val currentUser = auth.currentUser?.uid
            ?: return false
        val snap = usersCollection
            .document(currentUser)
            .collection("following")
            .document(farmId)
            .get()
            .await()
        return snap.exists()
    }

    /**
     * Get the number of followers for a farm.
     */
    suspend fun getFollowersCount(farmId: String): Int {
        val snap = firestore
            .collection("farms")
            .document(farmId)
            .collection("followers")
            .get()
            .await()
        return snap.size()
    }

    /**
     * Get the number of users following this farm.
     */
    suspend fun getFollowingCount(userId: String): Int {
        val snap = firestore
            .collection("users")
            .document(userId)
            .collection("following")
            .get()
            .await()
        return snap.size()
    }


}
