package dev.korryr.agrimarket.ui.navigation

sealed class Screen(
    val route: String
) {
    object Welcome : Screen("welcome")
    object SignIn : Screen("signIn")
    object SignUp : Screen("signUp")
    object Admin : Screen("admin")
    object ForgotPassword : Screen("forgotPassword")
    object Home : Screen("home")
    object MarketPlace : Screen("marketplace")
    object Education : Screen("education")
    object Community : Screen("community")
    object Profile : Screen("profile")
    object Post : Screen("post")

}