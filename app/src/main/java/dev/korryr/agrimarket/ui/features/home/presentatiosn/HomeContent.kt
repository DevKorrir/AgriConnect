package dev.korryr.agrimarket.ui.features.home.presentatiosn

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.korryr.agrimarket.R
import dev.korryr.agrimarket.ui.features.home.model.DashboardItem
import dev.korryr.agrimarket.ui.features.home.model.NavItem
import dev.korryr.agrimarket.ui.features.home.model.TaskItem
import dev.korryr.agrimarket.ui.features.bottomNav.view.CuteBottomNavBar


@Composable
fun HomeContent(
    dashboardItems: List<DashboardItem>,
    taskItems: List<TaskItem>,
    contentPadding: PaddingValues
) {
    Scaffold(
        bottomBar = {
            CuteBottomNavBar(
                items = listOf(
                    NavItem("Home", Icons.Rounded.Home, "home"),
                    NavItem("Marketplace", Icons.Rounded.ShoppingCart, "market"),
                    NavItem("Activity", Icons.Rounded.List, "activity"),
                    NavItem("Profile", Icons.Rounded.AccountCircle, "profile")
                ),
                selectedItemIndex = 0,
                onItemSelected = { /* Handle item selection */ }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                //.padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Greeting section with cute styling
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
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
                        Text(
                            text = "Good Morning, 🌞",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "Farmer Korry!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Perfect day for farming! Your crops are doing well.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    // Weather indicator with cute style
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Weather icon (sun)
                        Icon(
                            painter = painterResource(id = R.drawable.sun),
                            contentDescription = "Sunny",
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            // Dashboard items in grid layout
            Text(
                text = "Farm Dashboard",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                items(dashboardItems) { item ->
                    DashboardItemCard(item)
                }
            }

            // Task section with cute styling
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
            )

            taskItems.forEach { task ->
                TaskItemCard(task)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tips section with cute styling
            Text(
                text = "Farming Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
            )

            // Scrollable tips cards
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                items(3) { index ->
                    TipCard(
                        tipNumber = index + 1,
                        title = when (index) {
                            0 -> "Water Conservation"
                            1 -> "Pest Management"
                            else -> "Soil Health"
                        }
                    )
                }
            }

            // Bottom spacer for FAB clearance
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}