package dev.korryr.agrimarket.ui.features.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.People
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dev.korryr.agrimarket.appDrawer.presentation.AdminDrawerContent
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthViewModel
import dev.korryr.agrimarket.ui.features.home.model.DashboardItem
import dev.korryr.agrimarket.ui.features.home.model.NavItem
import dev.korryr.agrimarket.ui.features.home.model.TaskItem
import dev.korryr.agrimarket.ui.features.home.view.HomeContent
import dev.korryr.agrimarket.ui.features.topBar.CuteTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {},
    onLoggedOut: () -> Unit = {},
    scaffoldPadding: PaddingValues = PaddingValues()
) {
    val navController = rememberNavController()



    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Selected item for bottom navigation
    var selectedNavItem by remember { mutableIntStateOf(0) }

    // Notification count
    val notificationCount = 3

    // Drawer navigation items
    // In HomePage.kt
    val drawerItems = listOf(
        NavItem("Home", Icons.Rounded.Home, route = "Home"),
        NavItem("Marketplace", Icons.Rounded.ShoppingCart, route = "market"),
        NavItem("Orders", Icons.AutoMirrored.Rounded.List, route = "orders"),
        NavItem("Messages", Icons.AutoMirrored.Rounded.Message, route = "message"),
        NavItem("Profile", Icons.Rounded.AccountCircle, route = "profile"),
        NavItem("Settings", Icons.Rounded.Settings, route = "settings"),
        // Admin-only
        NavItem("User Management", Icons.Rounded.People, route = "admin_users"),
        NavItem("Reports", Icons.Rounded.Assessment, route = "admin_reports")

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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                AdminDrawerContent(
                    items = drawerItems,
                    onItemClick = { route ->
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigate(route)
                    },
                    currentRoute = "Home",

                    onLoggedOut = onLoggedOut

                )
            }
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
        ) { innerPaddingValues ->

            val combinedPadding = PaddingValues(
                top = innerPaddingValues.calculateTopPadding(),
//                bottom = max(
//                    innerPaddingValues.calculateBottomPadding(),
//                    scaffoldPadding.calculateBottomPadding()
//                ),
                start = innerPaddingValues.calculateStartPadding(
                    LocalLayoutDirection.current
                ),
                end = innerPaddingValues.calculateEndPadding(LocalLayoutDirection.current)

            )
            HomeContent(
                dashboardItems = dashboardItems,
                taskItems = taskItems,
                //modifier = Modifier.padding(innerPaddingValues),
                contentPadding = combinedPadding
            )
        }
    }
}
//crtl alt l