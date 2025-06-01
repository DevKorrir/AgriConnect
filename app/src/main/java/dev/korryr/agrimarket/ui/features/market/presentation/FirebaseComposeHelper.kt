package dev.korryr.agrimarket.ui.features.market.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

///////////////////////////////////////////////////////////////////////////
// -------------- 1) Fetch a single FarmProfile by farmId --------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberFarmProfile(
    farmId: String
): State<FarmProfile?> {
    val firestore = FirebaseFirestore.getInstance()
    val profileState = remember { mutableStateOf<FarmProfile?>(null) }

    DisposableEffect(farmId) {
        // Listen for changes at "farm_profiles/{farmId}"
        val subscription = firestore
            .collection("farms")
            .document(farmId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // You can log error.message if you want
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    profileState.value = snapshot.toObject(FarmProfile::class.java)
                } else {
                    profileState.value = null
                }
            }

        onDispose { subscription.remove() }
    }

    return profileState
}


///////////////////////////////////////////////////////////////////////////
//-------------- 2) Fetch “like count” for a post --------------
///////////////////////////////////////////////////////////////////////////

@Composable
fun rememberLikeCount(postId: String): State<Int> {
    val firestore = FirebaseFirestore.getInstance()
    val likeCountState = remember { mutableStateOf(0) }

    DisposableEffect(postId) {
        // Listen to "farm_posts/{postId}/likes"
        val subscription = firestore
            .collection("farm_posts")       // <- correct collection for posts
            .document(postId)
            .collection("likes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                likeCountState.value = snapshot?.size() ?: 0
            }

        onDispose { subscription.remove() }
    }

    return likeCountState
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
        awaitDispose { subscription.remove() }
    }
}
///////////////////////////////////////////////////////////////////////////
// -------------- 4) Toggle “like” for current user on a post --------------
///////////////////////////////////////////////////////////////////////////

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
        awaitDispose { subscription.remove() }
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
        awaitDispose { subscription.remove() }
    }
}

///////////////////////////////////////////////////////////////////////////
// -------------- 7) Toggle “bookmark” for current user on a post -------------
///////////////////////////////////////////////////////////////////////////

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
        awaitDispose { subscription.remove() }

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
        awaitDispose { subscription.remove() }
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
