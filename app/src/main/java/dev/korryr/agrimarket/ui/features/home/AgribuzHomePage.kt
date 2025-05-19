package dev.korryr.agrimarket.ui.features.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.korryr.agrimarket.ui.features.home.model.DashboardItem
import dev.korryr.agrimarket.ui.features.home.model.NavItem
import dev.korryr.agrimarket.ui.features.home.model.TaskItem
import dev.korryr.agrimarket.ui.features.home.presentatiosn.AdminDrawerContent
import dev.korryr.agrimarket.ui.features.home.view.HomeContent
import dev.korryr.agrimarket.ui.features.topBar.CuteTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    onNavigate: (String) -> Unit = {}
) {
    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Selected item for bottom navigation
    var selectedNavItem by remember { mutableIntStateOf(0) }

    // Notification count
    val notificationCount = 3

    // Drawer navigation items
    val drawerItems = listOf(
        NavItem("Dashboard", Icons.Rounded.Home, "dashboard"),
        NavItem("My Profile", Icons.Rounded.AccountCircle, "profile"),
        NavItem("My Farms", Icons.Rounded.List, "farms"),
        NavItem("Orders", Icons.Rounded.ShoppingCart, "orders"),
        NavItem("Settings", Icons.Rounded.Settings, "settings")
    )
    // Sample dashboard items
    val dashboardItems = listOf(
        DashboardItem("Crop Status", "75%", Color(0xFF9CCC65), "All crops growing well"),
        DashboardItem("Weather", "Sunny", Color(0xFFFFB74D), "30°C, Light breeze"),
        DashboardItem("Water Usage", "65%", Color(0xFF4FC3F7), "Efficiency improved by 10%"),
        DashboardItem("Harvest", "2 Days", Color(0xFFBA68C8), "Tomatoes ready soon")
    )

    // Sample task items
    val taskItems = listOf(
        TaskItem("Water tomato plants", "Today", true),
        TaskItem("Apply fertilizer", "Tomorrow", false),
        TaskItem("Check irrigation system", "Today", false),
        TaskItem("Harvest corn", "3 days", false)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                items = drawerItems,
                onItemClick = { route ->
                    scope.launch {
                        drawerState.close()
                    }
                    onNavigate(route)
                }
            )
        }
    ) {
        Scaffold(
                topBar = {
                    CuteTopAppBar(
                        title = "Agribuz Farm",
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        notificationCount = notificationCount
                    )
                },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* Handle add action */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    //contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add"
                    )
                }
            }
        ) { paddingValues ->
            HomeContent(
                dashboardItems = dashboardItems,
                taskItems = taskItems,
                //modifier = Modifier.padding(paddingValues)
                contentPadding = paddingValues
            )
        }
    }
}