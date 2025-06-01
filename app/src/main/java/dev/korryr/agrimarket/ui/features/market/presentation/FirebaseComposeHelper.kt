package dev.korryr.agrimarket.ui.features.market.presentation

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

///////////////////////////////////////////////////////////////////////////
// -------------- 1) Fetch a single FarmProfile by farmId --------------
///////////////////////////////////////////////////////////////////////////

//@SuppressLint("ProduceStateDoesNotAssignValue")
@Composable
fun rememberFarmProfile(
    farmId: String
): State<FarmProfile?> {
    val firestore = FirebaseFirestore.getInstance()
    return produceState<FarmProfile?>(initialValue = null, key1 = farmId) {
        //listen for changes to document"farm_profile/{farmId}"
        val subscription = firestore
            .collection("farm_profile")
            .document(farmId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    //log error if possible
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(FarmProfile::class.java)
                    value = profile
                } else {
                    value = null
                }
            }
        awaitClose { subscription.remove() }
    }
}


///////////////////////////////////////////////////////////////////////////
//-------------- 2) Fetch “like count” for a post --------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberLikeCount(postId: String): State<Int> {
    val firestore = FirebaseFirestore.getInstance()
    return produceState(initialValue = 0, key1 = postId) {
        val subscription = firestore
            .collection("farms")
            .document(postId)
            .collection("likes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    //log error if possible
                    return@addSnapshotListener
                }
                value = snapshot?.size() ?: 0
            }
        awaitClose { subscription.remove() }
    }

}

///////////////////////////////////////////////////////////////////////////
// -------------- 3) Check if current user has liked this post --------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberUserLiked(
    postId: String
): State<Boolean> {
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    return produceState(initialValue = false, key1 = postId, key2 = currentUser) {
        if (currentUser == null) {
            value = false
            return@produceState
        }
        val subscription = firestore
            .collection("farms")
            .document(postId)
            .collection("likes")
            .document(currentUser)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    //log error if possible
                    return@addSnapshotListener
                }
                // if the doc exists, the user has liked the post
                value = (snapshot != null && snapshot.exists())
            }
        awaitClose { subscription.remove() }
    }
}
///////////////////////////////////////////////////////////////////////////
// -------------- 4) Toggle “like” for current user on a post --------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun toggleLike(
    postId: String
) {
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
    if (currentUser.isBlank()) return

    val likeDoc = firestore
        .collection("farm_posts")
        .document(postId)
        .collection("likes")
        .document(currentUser)

    //user a transaction or simple get() and set() / delete() to avoid race conditions

    likeDoc.get().addOnSuccessListener { documentSnapshot ->
        if (documentSnapshot.exists()) {
            // Already liked remove (Unlike)
            likeDoc.delete()
        } else {
            // not liked, add doc (Like)
            likeDoc.set(mapOf("timestamp" to FieldValue.serverTimestamp()))
        }
    }

}

///////////////////////////////////////////////////////////////////////////
// -------------- 5) Fetch “comment count” for a post --------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberCommentCount(postId: String): State<Int> {
    val firestore = FirebaseFirestore.getInstance()
    return produceState(initialValue = 0, key1 = postId) {
        val subscription = firestore
            .collection("farm_posts")
            .document(postId)
            .collection("comments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    //log error if possible
                    return@addSnapshotListener
                }
                value = snapshot?.size() ?: 0
            }
        awaitClose { subscription.remove() }
    }
}

///////////////////////////////////////////////////////////////////////////
// -------------- 6) Check if current user has bookmarked this post ----------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberBookmarked(
    postId: String
): State<Boolean> {
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    return produceState(initialValue = false, key1 = postId, key2 = currentUser) {
        if (currentUser == null) {
            value = false
            return@produceState
        }
        val subscription = firestore
            .collection("users")
            .document(currentUser)
            .collection("bookmarks")
            .document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    //log error if possible
                    return@addSnapshotListener
                }
                // if the doc exists, the user has bookmarked the post
                value = (snapshot != null && snapshot.exists())
            }
        awaitClose { subscription.remove() }
    }
}

///////////////////////////////////////////////////////////////////////////
// -------------- 7) Toggle “bookmark” for current user on a post -------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun toggleBookMark(
    postId: String
) {
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: return
    if (currentUser.isBlank()) return

    val bookmarkDoc = firestore
        .collection("users")
        .document(currentUser)
        .collection("bookmarks")
        .document(postId)

    bookmarkDoc.get().addOnSuccessListener { documentSnapshot ->
        if (documentSnapshot.exists()) {
            // Already bookmarked remove (Unbookmark)
            bookmarkDoc.delete()
        } else {
            // not bookmarked, add doc (Bookmark)
            bookmarkDoc.set(mapOf("timestamp" to FieldValue.serverTimestamp()))
        }
    }
}

///////////////////////////////////////////////////////////////////////////
// -------------- 8) Fetch number of followers of a farm (optional) -------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberFollowerCount(farmId: String): State<Int> {
    val firestore = FirebaseFirestore.getInstance()
    return produceState(initialValue = 0, key1 = farmId) {

        val subscription = firestore
            .collection("users")
            .whereArrayContains("following", farmId)
        // or if you store in subcollection:
        // .collection("users").document(farmerUid).collection("followers")
            .addSnapshotListener{ snapshot, error ->
                if (error != null) return@addSnapshotListener
                value = snapshot?.size() ?:0

            }
        awaitClose { subscription.remove() }

    }


}

///////////////////////////////////////////////////////////////////////////
//  -------------- 9) Check if current user follows this farm --------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberUserFollows(farmId: String): State<Boolean> {
    val firestore = FirebaseFirestore.getInstance()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid
    return produceState(initialValue = false, key1 = farmId, key2 = currentUid) {
        if (currentUid == null) {
            value = false
            return@produceState
        }
        // Check if a document exists at `users/{currentUid}/following/{farmId}`
        val subscription = firestore
            .collection("users")
            .document(currentUid)
            .collection("following")
            .document(farmId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                value = (snapshot != null && snapshot.exists())
            }
        awaitClose { subscription.remove() }
    }
}

///////////////////////////////////////////////////////////////////////////
// -------------- 10) Toggle “follow” (follow/unfollow) for this farm --------------
///////////////////////////////////////////////////////////////////////////

fun toggleFollow(farmId: String) {
    val firestore = FirebaseFirestore.getInstance()
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    if (currentUid.isBlank()) return

    val followDoc = firestore
        .collection("users")
        .document(currentUid)
        .collection("following")
        .document(farmId)

    followDoc.get().addOnSuccessListener { snap ->
        if (snap.exists()) {
            followDoc.delete()
        } else {
            followDoc.set(mapOf("timestamp" to FieldValue.serverTimestamp()))
        }
    }
}
