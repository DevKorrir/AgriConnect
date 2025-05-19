package dev.korryr.agrimarket.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.korryr.agrimarket.ui.features.adminRole.view.AdminPanel
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.login.AgribuzLoginScreen
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.register.AgribuzSignupScreen
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthUiState
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthViewModel
import dev.korryr.agrimarket.ui.features.auth.preferences.AuthPreferencesRepository
import dev.korryr.agrimarket.ui.features.bottomNav.viewModel.BottomScreens
import dev.korryr.agrimarket.ui.features.home.HomePage
import dev.korryr.agrimarket.ui.features.market.view.MarketScreen
import dev.korryr.agrimarket.ui.features.welcome.AgribuzWelcomeScreen
import dev.korryr.agrimarket.ui.features.messages.view.MessageScreen
import dev.korryr.agrimarket.ui.features.orders.view.OrderScreen
import dev.korryr.agrimarket.ui.features.profile.view.ProfileScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    val context = LocalContext.current

    //obtain prefs repo
    val authPreferencesRepository = AuthPreferencesRepository(context)
    //collect logged in state
    val isLoggedIn  by authPreferencesRepository.isLoggedIn.collectAsState(initial = false)

    //viewmodel to observer auth state for role bases navigation
    val authViewModel : AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    //declare route
    val startRoute = when {
        isLoggedIn -> Screen.Home.route
        else -> Screen.Home.route
    }


    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = modifier
    ){
        composable(Screen.Welcome.route){
            AgribuzWelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }
        composable(Screen.SignIn.route) {
            LaunchedEffect(authState) {
                when (val state = authState) {

                    is AuthUiState.SuccessWithRole -> {
                        if (state.role == "ADMIN") navController.navigate(Screen.Admin.route) { popUpTo(Screen.SignIn.route){ inclusive = true } }
                        else navController.navigate(Screen.Home.route)    { popUpTo(Screen.SignIn.route){ inclusive = true } }
                    }

//                    is AuthUiState.SuccessWithRole -> {
//                        when (state.role.uppercase()) {
//                            "ADMIN" -> navController.navigate(Screen.Admin.route) {
//                                popUpTo(Screen.SignIn.route) { inclusive = true }
//                            }
//
//                            "FARMER", "SELLER" -> navController.navigate(Screen.Home.route) {
//                                popUpTo(Screen.SignIn.route) { inclusive = true }
//                            }
//
//                            else -> {
//                                Toast.makeText(context, "Unrecognized role: ${state.role}", Toast.LENGTH_LONG).show()
//                                // You could optionally navigate to a generic screen or logout.
//                                navController.navigate(Screen.SignIn.route) {
//                                    popUpTo(Screen.SignIn.route) { inclusive = true }
//                                }
//                            }
//                        }
//                    }

                    is AuthUiState.Success -> {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }

                    is AuthUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }

            AgribuzLoginScreen(
                onNavigateToSignup = {
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = {
                    email, password, isAdminLogin -> authViewModel.login(email, password, isAdminLogin)
                },
                onForgotPassword = {  /*navihgate to forgot password*/},
                onGoogleSignIn = {
                    Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                },
                navController = navController
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

        composable(Screen.Admin.route) {
            AdminPanel()
        }

        composable(BottomScreens.MarketPlace.route) {
            MarketScreen()
        }
        composable(BottomScreens.Message.route) {
            MessageScreen()
        }
        composable(BottomScreens.Orders.route) {
            OrderScreen()
        }
        composable(BottomScreens.Profile.route) {
            ProfileScreen()
        }

    }
}