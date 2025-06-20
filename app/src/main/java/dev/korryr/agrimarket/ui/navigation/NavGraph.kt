package dev.korryr.agrimarket.ui.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.korryr.agrimarket.ui.features.adminRole.view.AdminPanel
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.login.AgribuzLoginScreen
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.onForgotPassword.ForgotPasswordScreen
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.register.AgribuzSignupScreen
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthUiState
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthViewModel
import dev.korryr.agrimarket.ui.features.auth.preferences.AuthPreferencesRepository
import dev.korryr.agrimarket.ui.features.bottomNav.viewModel.BottomScreens
import dev.korryr.agrimarket.ui.features.farm.view.FarmProfileScreen
import dev.korryr.agrimarket.ui.features.home.HomePage
import dev.korryr.agrimarket.ui.features.market.view.MarketScreen
import dev.korryr.agrimarket.ui.features.messages.view.MessageScreen
import dev.korryr.agrimarket.ui.features.orders.view.OrderScreen
import dev.korryr.agrimarket.ui.features.posts.view.CreatePostScreen
import dev.korryr.agrimarket.ui.features.settings.view.SettingsScreen
import dev.korryr.agrimarket.ui.features.settings.view.apperances.view.AppearanceScreen
import dev.korryr.agrimarket.ui.features.welcome.AgribuzWelcomeScreen
import dev.korryr.agrimarket.ui.theme.ThemeViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    scaffoldPadding: PaddingValues = PaddingValues(),
    themeManager: ThemeViewModel
) {
    val context = LocalContext.current

    //obtain prefs repo
    val authPreferencesRepository = AuthPreferencesRepository(context)
    //collect logged in state
    val isLoggedIn by authPreferencesRepository.isLoggedIn.collectAsState(initial = false)

    //viewmodel to observer auth state for role bases navigation
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    //declare route
    val startRoute = when {
        isLoggedIn -> Screen.Home.route
        else -> Screen.Welcome.route
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val bottomItems = listOf(
        BottomScreens.Home,
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

    Scaffold(
        //contentWindowInsets = WindowInsets.safeContent,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar, //bottomBarState.value,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                content = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                        tonalElevation = 8.dp
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        bottomItems.forEach { screen ->
                            val selected =
                                currentDestination?.hierarchy?.any { it.route == screen.route } == true

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
        },
    ) { paddingValues ->


        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(Screen.Welcome.route) {
                AgribuzWelcomeScreen(
                    onGetStartedClick = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }
            composable(Screen.Login.route) {
                LaunchedEffect(authState) {
                    when (val state = authState) {

                        is AuthUiState.SuccessWithRole -> {
                            if (state.role == "ADMIN") navController.navigate(Screen.Admin.route) {
                                popUpTo(
                                    Screen.Login.route
                                ) { inclusive = true }
                            }
                            else navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
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
                                popUpTo(Screen.Login.route) { inclusive = true }
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
                    onLoginSuccess = { email, password, isAdminLogin ->
                        authViewModel.login(email, password, isAdminLogin)
                    },
                    onForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onGoogleSignIn = {
                        Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show()
                    },
                    navController = navController
                )
            }

            composable(Screen.SignUp.route) {
                AgribuzSignupScreen(
                    onSignedUp = {
                        navController.navigate(Screen.Login.route)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route)
                    }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )

            }

            composable(Screen.Home.route) {
                Box(
                    modifier = Modifier.padding(scaffoldPadding)
                ) {
                    HomePage(
                        onNavigate = { route ->
                            navController.navigate(route)
                        },
                        onLoggedOut = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        // Pass the scaffold padding to HomePage
                        // scaffoldPadding = scaffoldPadding
                    )
                }
            }

            composable(Screen.Admin.route) {
                AdminPanel()
            }

            composable(BottomScreens.MarketPlace.route) {
                MarketScreen(
                    navController = navController
                )
            }
            composable(BottomScreens.Message.route) {
                MessageScreen()
            }

            composable(BottomScreens.Orders.route) {
                OrderScreen()
            }

            composable(BottomScreens.Profile.route) {
                FarmProfileScreen(
                    onBackPressed = {
                        navController.navigateUp()
                    },
                    onNavigateToPostScreen = {
                        navController.navigate(Screen.Post.route)
                    }
                )

            }


            composable(Screen.Post.route) {
                CreatePostScreen (
                    onPostSuccess = {
                        navController.navigateUp()
                    },
                    navConctroller = navController
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = navController,
                    onAppearanceClick = {
                        // Navigate to appearance/theme screen
                        navController.navigate(Screen.Appearance.route)
                    }
                )
            }

            composable(Screen.Appearance.route) {
                AppearanceScreen(
                    themeManager = themeManager
                )
            }








        }
    }
}
