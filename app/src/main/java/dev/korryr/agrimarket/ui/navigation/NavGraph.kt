package dev.korryr.agrimarket.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.login.AgribuzLoginScreen
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.register.AgribuzSignupScreen
import dev.korryr.agrimarket.ui.features.auth.preferences.AuthPreferencesRepository
import dev.korryr.agrimarket.ui.features.home.HomePage
import dev.korryr.agrimarket.ui.features.welcome.AgribuzWelcomeScreen

@Composable
fun NavGraph(
    navController: NavHostController
){
    val context = LocalContext.current
    //obtain prefs repo
    val authPreferencesRepository = AuthPreferencesRepository(context)

    //collect logged in state
    val isLoggedIn  by authPreferencesRepository.isLoggedIn.collectAsState(initial = false)

    //declare route
    val startRoute = if (isLoggedIn) Screen.Home.route else Screen.Welcome.route

    NavHost(
        navController = navController,
        startDestination = startRoute,
        //modifier = Modifier.padding(it)
    ){
        composable(Screen.Welcome.route){
            AgribuzWelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }
        composable(Screen.SignIn.route) {
            AgribuzLoginScreen(
                onNavigateToSignup = {
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route)
                },
                onForgotPassword = {  },
                onGoogleSignIn = {
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                },
            )
        }

        composable(Screen.SignUp.route) {
            AgribuzSignupScreen(
                onSignedUp = {
                    navController.navigate(Screen.SignIn.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }

        composable(Screen.Home.route) {
            HomePage()
        }

    }
}