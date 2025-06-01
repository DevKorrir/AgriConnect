package dev.korryr.agrimarket.ui.features.market.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import kotlin.random.Random
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import dev.korryr.agrimarket.ui.features.market.viewModel.MarketViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import dev.korryr.agrimarket.ui.theme.SecondaryTextLight
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.market.presentation.rememberFarmProfile
import dev.korryr.agrimarket.ui.features.market.presentation.rememberLikeCount
import dev.korryr.agrimarket.ui.features.market.presentation.rememberUserFollows
import dev.korryr.agrimarket.ui.features.market.presentation.rememberUserLiked
import dev.korryr.agrimarket.ui.features.market.presentation.toggleFollow
import dev.korryr.agrimarket.ui.features.market.presentation.toggleBookMark
import java.util.Calendar
import dev.korryr.agrimarket.ui.features.market.presentation.rememberFollowerCount
import dev.korryr.agrimarket.ui.features.market.presentation.rememberBookmarked
import dev.korryr.agrimarket.ui.features.market.presentation.rememberCommentCount
import dev.korryr.agrimarket.ui.features.market.presentation.toggleLike


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    marketViewModel: MarketViewModel = hiltViewModel(),
    onPostClick: (FarmPost) -> Unit = {},
    onProfileClick: (String) -> Unit = {},
    onFollowClick: (String) -> Unit = {},
    onLikeClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onBookmarkClick: (String) -> Unit = {}
) {

    var visible by remember { mutableStateOf(false) }

    // 1. Observe the posts and loading state
    val allPosts by marketViewModel.allPosts.collectAsState()
    val isLoading by marketViewModel.isLoading.collectAsState()

    // 2. Observe the dynamic list of types
    //    Prepend "All" so that tab 0 == "All"
    val rawTypes by marketViewModel.allTypes.collectAsState()
    val categories = remember(rawTypes) {
        listOf("All") + rawTypes
    }

    // 3. Track which tab is currently selected
    var selectedTabIndex by remember { mutableStateOf(0) }

    // 4. Compute displayedPosts based on selectedTabIndex
    val displayedPosts = remember(allPosts, selectedTabIndex, categories) {
        if (selectedTabIndex == 0) {
            allPosts
        } else {
            // categories[1] = first real type, etc.
            val chosenType = categories[selectedTabIndex]
            allPosts.filter { it.type.equals(chosenType, ignoreCase = true) }
        }
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { -40 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "AgriMarket",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {

                    // Enhanced TabRow with gradient and better styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(25.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        // ——— Dynamic TabRow ———
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    Box(
                                        modifier = Modifier
                                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.tertiary
                                                    )
                                                )
                                            )
                                    )
                                }
                            }
                        ) {
                            categories.forEachIndexed { index, category ->
                                Tab(
                                    selected = (selectedTabIndex == index),
                                    onClick = { selectedTabIndex = index },
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    text = {
                                        Text(
                                            category,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (selectedTabIndex == index)
                                                    FontWeight.SemiBold
                                                else
                                                    FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    //Spacer(modifier = Modifier.height(8.dp))

                    // ——— Loading / Empty / Grid ———
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        "Loading fresh products...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }
                        }

                        displayedPosts.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Inventory,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = if (selectedTabIndex == 0)
                                            "No posts available"
                                        else
                                            "No posts in “${categories[selectedTabIndex]}”",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        "Check back later for fresh products!",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(displayedPosts) { post ->
                                    MarketPostCard(
                                        post = post,
                                        onPostClick = { onPostClick(post) },
                                        onProfileClick = onProfileClick,
                                        onFollowClick = onFollowClick,
                                        onLikeClick = onLikeClick,
                                        onCommentClick = onCommentClick,
                                        onBookmarkClick = onBookmarkClick,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun MarketPostCard(
    post: FarmPost,
    onPostClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    onFollowClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onBookmarkClick: (String) -> Unit,
) {
    // fetch farmer profile in real time
    val profileState by rememberFarmProfile(post.farmId)
    val profileName = profileState?.farmName ?: "sijui farmer"
    val profileImageUrl = profileState?.imageUrl ?: ""

    // 2) Compute “days ago” from post.timestamp (if you stored a Firestore Timestamp)
    //    If your FarmPost has a `timestamp: com.google.firebase.Timestamp` field, do:
    //    val daysAgo by rememberDaysAgo(post.timestamp)
    //    For simplicity, here we’ll just show a placeholder if timestamp is missing.
    val daysAgoText = profileState?.let {
        post.timestamp?.let { ts ->
            val now = Calendar.getInstance().timeInMillis
            val then = ts.toDate().time
            val diffdays = ((now - then) / (1000 * 60 * 60 * 24)).toInt()
            "$diffdays d ago"
        } ?: ""
    } ?: ""

    // 3) Real-time like count and whether current user has liked
    val likeCount by rememberLikeCount(post.postId)
    val userLiked by rememberUserLiked(post.postId)

    // 4) Real-time comment count
    val commentCount by rememberCommentCount(post.postId)

    // 5) Real-time bookmark state
    val bookmarked by rememberBookmarked(post.postId)

    // 6) Real-time follow state (does current user follow this farm?)
    val followingFarm by rememberUserFollows(post.farmId)

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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                                painter = rememberAsyncImagePainter(profileImageUrl),
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                            )
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

                // Follow Button / unfollow
                OutlinedButton(
                    onClick = {
                        toggleFollow(post.farmId)
                        onFollowClick(post.farmId)
                    },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = if (followingFarm) null else BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (followingFarm)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent,
                        contentColor = if (followingFarm)
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
                            imageVector = if (followingFarm) Icons.Default.Check else Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            if (followingFarm) "Following" else "Follow",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
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
                        painter = rememberAsyncImagePainter(post.imageUrl),
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                } else {
                    // Placeholder when there’s no image URL
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
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
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "No image",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
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
                        modifier = Modifier.clickable {
                            toggleLike(post.postId)
                            onLikeClick(post.postId)
                        }
                    ) {
                        Icon(
                            imageVector = if (userLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (userLiked) Color.Red else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.7f
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "$likeCount",
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
                        modifier = Modifier.clickable { onCommentClick(post.description) }
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

                    // Share Button
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { /* Handle share */ }
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
                            toggleBookMark(post.postId)
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
//
//// Helper functions for mock data
//private fun generateMockUserName(): String {
//    val names = listOf(
//        "John Farmer", "Mary Gardens", "Peter Crops", "Sarah Fields",
//        "Mike Harvest", "Lucy Green", "David Plants", "Emma Organic",
//        "James Fresh", "Anna Natural", "Tom Healthy", "Lisa Pure"
//    )
//    return names.random()
//}
//
//private fun generateMockProfileImage(): String {
//    val avatars = listOf(
//        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
//        "https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face",
//        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face",
//        "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face",
//        "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face"
//    )
//    return avatars.random()
//}