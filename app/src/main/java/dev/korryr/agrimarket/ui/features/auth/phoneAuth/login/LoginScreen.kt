package dev.korryr.agrimarket.ui.features.auth.phoneAuth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.korryr.agrimarket.R
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthUiState
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthViewModel
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField

@Composable
fun AgribuzLoginScreen(
    onLoginSuccess: (String) -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onNavigateToSignup: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // State variables
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    // Validation states
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Form validity
    var isFormValid by remember { mutableStateOf(false) }

    // Icons
    val phoneIcon = painterResource(id = R.drawable.mail)
    val passwordIcon = painterResource(id = R.drawable.padlock)
    val googleIcon = painterResource(id = R.drawable.google)
    val authState by viewModel.authState.collectAsState()

    // React to successful login
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            onLoginSuccess((authState as AuthUiState.Success).user.uid)
        }
    }

//    // Validate form on input change
//    LaunchedEffect(email, password) {
//        var valid = true
//        if (email.isBlank()) {
//            emailError = "Email is required"
//            valid = false
//        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            emailError = "Enter a valid email"
//            valid = false
//        } else emailError = ""
//
//        if (password.isBlank()) {
//            passwordError = "Password is required"
//            valid = false
//        } else passwordError = ""
//
//        isFormValid = valid
//    }


    // Background gradient
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Farm imagery in background (top section)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.cabbage_backgroud),
                contentDescription = "Farm Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Semi-transparent overlay with cute gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
                            )
                        )
                    )
            )

            // App logo or icon with cute animation effect
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agribuzna),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(65.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome Back!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                )

                Text(
                    text = "Log in to your farming account",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card for login form with cute rounded corners
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Phone field with cute styling
                    AgribuzTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (emailError.isNotEmpty()) emailError = ""
                        },
                        label = "Email",
                        leadingIcon = phoneIcon,
                        error = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field with cute styling
                    AgribuzTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            //if (passwordError.isNotEmpty()) passwordError = ""
                        },
                        label = "Password",
                        leadingIcon = passwordIcon,
                        isPassword = true,
                        error = if (showErrors) passwordError else "",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Forgot password link with cute positioning
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onForgotPassword,
                            contentPadding = PaddingValues(4.dp)
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login button with cute gradient
                    Button(
                        onClick = {
                            showErrors = true
                            // Validate on click
                            var valid = true
                            if (email.isBlank()) {
                                emailError = "Email is required"
                                valid = false
                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailError = "Invalid email"
                                valid = false
                            } else emailError = ""

                            if (password.isBlank()) {
                                passwordError = "Password is required"
                                valid = false
                            } else passwordError = ""

                            isFormValid = valid
                            if (valid) viewModel.login(email, password)
                        },
                        enabled = authState !is AuthUiState.Loading,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (authState is AuthUiState.Loading) CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        ) else
                        Text(
                            text = "Log In",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // OR divider with cute styling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Text(
                            text = "  OR  ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Divider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google sign in button with cute design
                    OutlinedButton(
                        onClick = onGoogleSignIn,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.5.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = googleIcon,
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "Continue with Google",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Sign up text with cute animation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                TextButton(onClick = onNavigateToSignup) {
                    Text(
                        text = "Sign Up",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Cute farming footer image
            Image(
                painter = painterResource(id = R.drawable.guava_footer),
                contentDescription = "Crops",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 32.dp),
                contentScale = ContentScale.FillWidth,
                alpha = 0.8f
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

// Validation function
private fun validateForm(
    email: String,
    password: String,
    setEmailError: (String) -> Unit,
    setPasswordError: (String) -> Unit,
    setFormValid: (Boolean) -> Unit
) {
    var isValid = true

    // email validation
    if (email.trim().isEmpty()) {
        setEmailError("Email is required")
        isValid = false
    } else if (!isValidEmail(email)) {
        setEmailError("Enter a valid email address")
        isValid = false
    } else {
        setEmailError("")
    }

    // Password validation
    if (password.isEmpty()) {
        setPasswordError("Password is required")
        isValid = false
    } else {
        setPasswordError("")
    }

    setFormValid(isValid)
}

