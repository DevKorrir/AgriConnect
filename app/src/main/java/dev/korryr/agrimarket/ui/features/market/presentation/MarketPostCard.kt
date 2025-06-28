package dev.korryr.agrimarket.ui.features.market.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.market.viewModel.MarketViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import java.util.Calendar

@Composable
fun MarketPostCard(
    post: FarmPost,
    farmProfile: FarmProfile?,
    onPostClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    onFollowClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    marketViewModel: MarketViewModel = hiltViewModel(),
) {
    // fetch farmer profile in real time
    val profileName = farmProfile?.farmName ?: "Unknown"
    val profileImageUrl = farmProfile?.imageUrl ?: ""

    // Image loading states for post image
    val postImagePainter = rememberAsyncImagePainter(
        model = post.imageUrl,
    )

    // Image loading states for profile image
    val profileImagePainter = rememberAsyncImagePainter(
        model = profileImageUrl,
    )

    // 2) Compute “days ago” from post.timestamp (if you stored a Firestore Timestamp)
    //    If your FarmPost has a `timestamp: com.google.firebase.Timestamp` field, do:
    //    val daysAgo by rememberDaysAgo(post.timestamp)
    //    For simplicity, here we’ll just show a placeholder if timestamp is missing.
    val daysAgoText = post.timestamp.let {
        post.timestamp.let { tsInMillis: Long ->
            val now = Calendar.getInstance().timeInMillis
            val then = tsInMillis
            val diffDays = ((now - then) / (1000 * 60 * 60 * 24)).toInt()

            // Handle cases for today, yesterday, etc. for better UX if desired
            when {
                diffDays == 0 -> "Today"
                diffDays == 1 -> "Yesterday"
                diffDays < 0 -> "In the future?" // Or handle as an error/edge case
                else -> "$diffDays d ago"
            }
        } ?: "Unknown"// fallback if post.timestamp is null
    } ?: "" // fall back if profile ids null

    //val likeCount by marketViewModel.selectedLikeCount.collectAsState()
    //val userLiked by marketViewModel.selectedUserLiked.collectAsState()
    val commentCount by marketViewModel.selectedCommentCount.collectAsState()
    val bookmarked by marketViewModel.selectedBookmarked.collectAsState()
    //val isFollowing by marketViewModel.selectedUserFollows.collectAsState()
    val isFollowing by marketViewModel.isFollowing.collectAsState()

    //get current user id
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // check if is my post
    val isMyPost by remember {
        derivedStateOf {
            currentUserId == post.farmId
        }
    }

    LaunchedEffect(post.postId) {
        marketViewModel.observePostInteractions(post.postId)
    }

    // Collect states - these will be consistent and won't flicker
    val likeState by marketViewModel.getLikeStateForPost(post.postId).collectAsState()

    // Extract individual properties to avoid showing the whole object
    val likeCount = likeState.likeCount
    val isLiked = likeState.isLiked
    //val commentCount = likeState.commentCount
    val isBookmarked = likeState.isBookmarked

    LaunchedEffect(post.farmId) {
        marketViewModel.refreshFollowState(post.farmId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header with Profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick(post.farmId) }
                ) {
                    // Profile Image with gradient border
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                ),
                                shape = CircleShape
                            )
                            .padding(2.dp)
                    ) {


                        if (profileImageUrl.isNotBlank()) {

                            Image(
                                painter = profileImagePainter,
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                            )

                            // Show loading indicator if the image is in loading state
                            if (profileImagePainter.state is AsyncImagePainter.State.Loading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        } else {
                            // If no profile image URL, show a placeholder circle
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "No Profile",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = profileName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = daysAgoText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isMyPost) {
                        // Option A: show nothing
                        Spacer(modifier = Modifier.width(16.dp))

//                        // Option B: show a disabled “Owner” badge instead
//                        Card(
//                            shape = RoundedCornerShape(20.dp),
//                            colors = CardDefaults.cardColors(
//                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
//                            ),
//                            border = BorderStroke(
//                                1.dp,
//                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
//                            )
//                        ) {
//                            Row(
//                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(4.dp)
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.KingBed,
//                                    contentDescription = "Owner",
//                                    tint = MaterialTheme.colorScheme.primary,
//                                    modifier = Modifier.size(14.dp)
//                                )
//                                Text(
//                                    text = "Owner",
//                                    style = MaterialTheme.typography.labelSmall.copy(
//                                        fontWeight = FontWeight.SemiBold,
//                                        fontSize = 11.sp
//                                    ),
//                                    color = MaterialTheme.colorScheme.primary
//                                )
//                            }
//                        }
                    } else {
                        // Follow Button / unfollow
                        OutlinedButton(
                            onClick = { onFollowClick(post.farmId) },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(18.dp),
                            border = if (isFollowing) null else BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFollowing)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Transparent,
                                contentColor = if (isFollowing)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    if (isFollowing) "Following" else "Follow",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }


            // 1. Top: Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clickable { onPostClick() }
            ) {

                if (post.imageUrl.isNotBlank()) {
                    Image(
                        painter = postImagePainter,
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )

                    if (postImagePainter.state is AsyncImagePainter.State.Loading) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 4.dp
                            )
                        }
                    }
                } else {
                    // Placeholder when there’s no image URL
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "No image",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(80.dp)
                            )
                            Text(
                                "No Image Available",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }

                // Category badge overlay
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    )
                ) {
                    Text(
                        text = post.type,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Action Buttons Row {like, comment, share, boorkmark }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like Button =count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                onLikeClick(post.postId)
                            }
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.7f
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = likeCount.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        )
                    }

                    // Comment Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onCommentClick(post.description) }
                            .padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "$commentCount",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        )
                    }

                    val context = LocalContext.current
                    // Share Button
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(24.dp)
                            .clickable {
                                onShareClick(post.postId)
                            }
                            .padding(2.dp)
                    )
                }

                // Bookmark Button
                Icon(
                    imageVector = if (bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (bookmarked) MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onBookmarkClick(post.postId)
                        }
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Product Details (descriptio price and quantity)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price
                    Text(
                        text = "KES ${"%,.2f".format(post.price)}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp
                        )
                    )

                    // Quantity badge
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "${post.quantity} ${post.unit}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    }
                }
            }
        }
    }
}