package dev.korryr.agrimarket.ui.features.home.view

import android.icu.util.Calendar
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import dev.korryr.agrimarket.R
import dev.korryr.agrimarket.ui.features.home.model.DashboardItem
import dev.korryr.agrimarket.ui.features.home.model.TaskItem
import dev.korryr.agrimarket.ui.features.home.presentatiosn.CategoryChip
import dev.korryr.agrimarket.ui.features.home.presentatiosn.DashboardItemCard
import dev.korryr.agrimarket.ui.features.home.presentatiosn.TaskItemCard
import dev.korryr.agrimarket.ui.features.home.presentatiosn.TipCard


@Composable
fun HomeContent(
    dashboardItems: List<DashboardItem>,
    taskItems: List<TaskItem>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val userName = user?.displayName ?: "Guest"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        // 1. Search Bar
        var query by remember { mutableStateOf("") }
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
                Icon(Icons.Default.Search, contentDescription = "Search")
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { /* handle search */ }
                    ),
                    decorationBox = { inner ->
                        if (query.isEmpty()) Text("Search products, tasks…", color = Color.Gray)
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        // 2. Category Chips
        val categories = listOf("Seeds", "Fertilizers", "Equipment", "Livestock")

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                CategoryChip(label = cat) { /* onCategorySelected(cat) */ }
            }
        }
        Spacer(Modifier.height(16.dp))

        // Greeting section with cute styling
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ),
        ) {
            val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) // same as modulus 24
            val greeting = when (currentTime) {
                in 3..11 -> "Good Morning ☀️"
                in 12..15 -> "Good Afternoon 🌇"
                in 16..20 -> "Good Evening 🌆"
                else -> "Good Night 🌃"
            }

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
                        text = greeting,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = userName,
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