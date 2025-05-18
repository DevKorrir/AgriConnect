package dev.korryr.agrimarket.ui.features.bottomNav.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object MarketPlace : Screen(
        route = "home",
        icon = Icons.Outlined.Home,
        title = "Home"
    )

    object Education : Screen("market", Icons.Outlined.Shop, "Market")
    object Orders : Screen("purchasing", Icons.AutoMirrored.Outlined.List, "Orders")
    object Message: Screen("message", Icons.AutoMirrored.Outlined.Message, "Messages")
    object Profile : Screen("account", Icons.Outlined.AccountCircle, "Profile")
}