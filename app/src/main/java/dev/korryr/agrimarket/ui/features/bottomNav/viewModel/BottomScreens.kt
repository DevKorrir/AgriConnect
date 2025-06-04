package dev.korryr.agrimarket.ui.features.bottomNav.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Shop
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomScreens(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomScreens(
        route = "home",
        icon = Icons.Outlined.Home,
        title = "Home"
    )

    object MarketPlace : BottomScreens("market", Icons.Outlined.Storefront, "Market")
    object Orders : BottomScreens("orders", Icons.AutoMirrored.Outlined.List, "Orders")
    object Message: BottomScreens("message", Icons.AutoMirrored.Outlined.Message, "Messages")
    object Profile : BottomScreens("profile", Icons.Outlined.AccountCircle, "Profile")
}