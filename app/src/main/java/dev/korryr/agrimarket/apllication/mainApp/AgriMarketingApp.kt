package dev.korryr.agrimarket.apllication.mainApp

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import dev.korryr.agrimarket.ui.navigation.NavGraph
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import dev.korryr.agrimarket.ui.features.bottomNav.viewModel.BottomScreens
import dev.korryr.agrimarket.ui.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun AgriMarketingApp(
    modifier: Modifier = Modifier
){
    val navController = rememberNavController()
    val bottomBarState = rememberSaveable { mutableStateOf(true) }

    // Drawer state
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Selected item for bottom navigation
    var selectedNavItem by remember { mutableIntStateOf(0) }
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Notification count
    val notificationCount = 3

    val bottomItems = listOf(
       BottomScreens.Home,
       // BottomScreens.MarketPlace,
        BottomScreens.MarketPlace,
        BottomScreens.Message,
        BottomScreens.Orders,
        BottomScreens.Profile
    )

    val showBottomBarPages = setOf(
        Screen.Home.route,
        BottomScreens.MarketPlace.route,
        BottomScreens.Message.route,
        BottomScreens.Orders.route,
        BottomScreens.Profile.route
    )

    val shouldShowBottomBar = remember(currentRoute) {
        currentRoute?.let { it in showBottomBarPages } ?: false
    }


    Scaffold (
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar, //bottomBarState.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                content = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        tonalElevation = 8.dp
                    ){
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        bottomItems.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                            NavigationBarItem(
                                selected = selected,
                                onClick = { navController.navigate(screen.route) },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (selected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        color = if (selected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                            )
                        }
                    }
                }
            )
        }


    ) { padding ->
        NavGraph(
            //modifier = Modifier.padding(padding),
            navController = navController
        )
   }

}