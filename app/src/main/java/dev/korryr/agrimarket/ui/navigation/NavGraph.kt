package dev.korryr.agrimarket.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.register.SignupScreen
import dev.korryr.agrimarket.ui.features.welcome.WelcomeScreen

@Composable
fun NavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        //modifier = Modifier.padding(it)
    ){
        composable(Screen.Welcome.route){
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignupScreen(
                onSignupComplete = { /* TODO: navigate to login or home */ },
                onBack = { navController.popBackStack() }
            )
        }

    }
}