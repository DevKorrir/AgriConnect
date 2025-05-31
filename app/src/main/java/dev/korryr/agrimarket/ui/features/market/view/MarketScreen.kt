package dev.korryr.agrimarket.ui.features.market.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import dev.korryr.agrimarket.ui.features.market.viewModel.MarketViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import dev.korryr.agrimarket.ui.theme.SecondaryTextLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    marketViewModel: MarketViewModel = hiltViewModel(),
    onPostClick: (FarmPost) -> Unit = {}
){

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
                        Text(
                            "Market",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // ——— Dynamic TabRow ———
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        categories.forEachIndexed { index, category ->
                            Tab(
                                selected = (selectedTabIndex == index),
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        category,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (selectedTabIndex == index)
                                                FontWeight.SemiBold
                                            else
                                                FontWeight.Normal
                                        )
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ——— Loading / Empty / Grid ———
                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        displayedPosts.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (selectedTabIndex == 0)
                                        "No posts available"
                                    else
                                        "No posts in “${categories[selectedTabIndex]}”",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                )
                            }
                        }

                        else -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(displayedPosts) { post ->
                                    MarketPostCard(post = post, onClick = { onPostClick(post) })
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top: Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
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
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "No image",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Body: Description & Price
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "KES ${"%,.2f".format(post.price)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "${post.quantity} ${post.unit}",
                        style = MaterialTheme.typography.bodySmall.copy(color = SecondaryTextLight)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Footer: Category Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.type, // e.g. “Livestock”, “Crops”
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}