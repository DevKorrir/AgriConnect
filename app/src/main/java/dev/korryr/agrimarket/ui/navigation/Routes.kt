package dev.korryr.agrimarket.ui.navigation

sealed class Screen(
    val route: String
) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signUp")
    object Admin : Screen("admin")
    object ForgotPassword : Screen("forgotPassword")
    object Home : Screen("home")
    object MarketPlace : Screen("marketplace")
    object Education : Screen("education")
    object Community : Screen("community")
    object Profile : Screen("profile")
    object Post : Screen("post")
    object Settings : Screen("settings")
    object Appearance : Screen("appearance")

}