package dev.korryr.agrimarket.ui.features.home.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.korryr.agrimarket.ui.theme.LightLeafGreen
import dev.korryr.agrimarket.ui.theme.TealGreen


@Composable
fun HomeContentShimmer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
    ) {

        // Search Bar Shimmer
        ShimmerSearchBar()
        Spacer(Modifier.height(16.dp))

        // Category Chips Shimmer
        ShimmerCategoryChips()
        Spacer(Modifier.height(16.dp))

        // Greeting Card Shimmer
        ShimmerGreetingCard()

        // Dashboard Title Shimmer
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .width(140.dp)
                .height(20.dp)
                .shimmerEffect()
        )

        // Dashboard Grid Shimmer
        ShimmerDashboardGrid()

        // Tasks Title Shimmer
        Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 12.dp)
                .width(120.dp)
                .height(20.dp)
                .shimmerEffect()
        )

        // Task Items Shimmer
        repeat(3) {
            ShimmerTaskItem()
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tips Title Shimmer
        Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 12.dp)
                .width(110.dp)
                .height(20.dp)
                .shimmerEffect()
        )

        // Tips Row Shimmer
        ShimmerTipsRow()
    }

}

@Composable
private fun ShimmerSearchBar() {
    Surface(
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .shimmerEffect()
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun ShimmerCategoryChips() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(4) {
            Box(
                modifier = Modifier
                    .width((60..100).random().dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun ShimmerGreetingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Greeting text shimmer
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(16.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Username shimmer
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(24.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description shimmer
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(14.dp)
                        .shimmerEffect()
                )
            }

            // Weather icon shimmer
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun ShimmerDashboardGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
    ) {
        items(4) {
            ShimmerDashboardItem()
        }
    }
}

@Composable
private fun ShimmerDashboardItem() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(14.dp)
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )
            }

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(20.dp)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun ShimmerTaskItem() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shimmerEffect(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .shimmerEffect()
                )
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun ShimmerTipsRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(3) {
            ShimmerTipCard()
        }
    }
}

@Composable
private fun ShimmerTipCard() {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .clip(CircleShape)
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .shimmerEffect()
                )
            }

            Column {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .shimmerEffect()
                )
            }
        }
    }
}


fun Modifier.shimmerEffect(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    )

    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = alpha, y = alpha)
        )//.copy(alpha = alpha)
    )

}