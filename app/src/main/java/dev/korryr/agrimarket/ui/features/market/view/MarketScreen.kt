package dev.korryr.agrimarket.ui.features.market.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import dev.korryr.agrimarket.ui.features.market.presentation.MarketPostCard
import dev.korryr.agrimarket.ui.features.market.viewModel.MarketViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost


@Composable
fun MarketScreen(
    marketViewModel: MarketViewModel = hiltViewModel(),
    onPostClick: (FarmPost) -> Unit = {},
    onProfileClick: (String) -> Unit = {},
    onFollowClick: (String) -> Unit = {},
    onLikeClick: (String) -> Unit = {},
    onCommentClick: (String) -> Unit = {},
    onBookmarkClick: (String) -> Unit = {},
    navController: NavHostController
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
    // If you want tabs, create your category list from allPosts.map { it.type }.distinct(), etc.
//    val categories = remember(allPosts) {
//        listOf("All") + allPosts.map { it.type }.distinct().filter { it.isNotBlank() }
//    }

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

    // b) Observe all farm profiles as a Map<farmId, FarmProfile>
    val farmProfiles by marketViewModel.allFarmProfiles.collectAsState()

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    )
                )
        ) {

            // Beautiful Top Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp),
                shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                navController.navigateUp()
                            }
                            .size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )

                    Text(
                        "SocialMarket",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Enhanced TabRow with gradient and better styling
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(25.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                // Beautiful Scrollable Category Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories.size) { index ->
                        val category = categories[index]
                        val isSelected = selectedTabIndex == index

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            label = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    ),
                                    maxLines = 1
                                )
                            },
                            modifier = Modifier
                                .height(40.dp)
                                .shadow(
                                    elevation = if (isSelected) 8.dp else 8.dp,
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                labelColor = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            border = if (!isSelected) {
                                FilterChipDefaults.filterChipBorder(
                                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    selectedBorderColor = Color.Transparent,
                                    borderWidth = 1.dp,
                                    enabled = !isSelected,
                                    selected = isSelected,
                                    //disabledBorderColor = Color.Transparent,
                                    // disabledSelectedBorderColor =Color.Transparent,
                                    selectedBorderWidth = 3.dp
                                )
                            } else null,
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            } else null
                        )
                    }
                }
            }

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
                                farmProfile = farmProfiles[post.farmId],
                                onPostClick = {
                                    //marketViewModel.selectPost(post.postId)
                                   // onPostClick(post)
                                },
                                onProfileClick = {
                                    //marketViewModel.selectFarm(post.farmId)
                                    //onProfileClick(post.farmId)
                                },

                                onFollowClick = { farmId ->
                                    marketViewModel.onToggleFollow(farmId)
                                },

                                onLikeClick = { postId ->
                                    //marketViewModel.onToggleLike(postId)
                                    //onLikeClick(postId)
                                },
                                onCommentClick = { postId ->
                                    //onCommentClick(postId)
                                },
                                onBookmarkClick = { postId ->
                                    //marketViewModel.onToggleBookmark(postId)
                                    //onBookmarkClick(postId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}