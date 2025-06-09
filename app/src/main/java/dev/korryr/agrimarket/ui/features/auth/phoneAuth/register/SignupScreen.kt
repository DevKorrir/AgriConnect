package dev.korryr.agrimarket.ui.features.auth.phoneAuth.register

import android.annotation.SuppressLint
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.korryr.agrimarket.R
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthUiState
import dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel.AuthViewModel
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField


@SuppressLint("ContextCastToActivity")
@Composable
fun AgribuzSignupScreen(
    onSignedUp: (uid: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // State variables
    var displayName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // Validation states
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    // Password strength (0-4)
    var passwordStrength by remember { mutableStateOf(0) }
    //var passwordScore by remember { mutableStateOf(0) }
    //val strengthLabel = passwordStrengthLabel(passwordScore)
    // Form validity
    var isFormValid by remember { mutableStateOf(false) }

    // Icons
    val userIcon = painterResource(id = R.drawable.user) // Replace with your user icon
    val emailIcon = painterResource(id = R.drawable.mail) // Replace with your phone icon
    val passwordIcon = painterResource(id = R.drawable.padlock)


    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            onSignedUp((authState as AuthUiState.Success).user.uid)
        }
    }


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
                .height(160.dp)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.cow_background), // Replace with your farm image
                contentDescription = "Farm Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Semi-transparent overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // App logo or icon
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.agribuzna), // Replace with your app logo
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .clip(CircleShape)
                            //.size(40.dp)
                    )
                }
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card for signup form
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Text(
                        text = "Join our Agribuz community",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Name field
                    AgribuzTextField(
                        value = displayName,
                        onValueChange = {
                            displayName = it
                            if (nameError.isNotEmpty()) nameError = ""
                        },
                        label = "Full Name",
                        leadingIcon = userIcon,
                        error = nameError,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone field
                    AgribuzTextField(
                        value = email,
                        leadingIcon = emailIcon,
                        onValueChange = {
                            email = it
                            if (emailError.isNotEmpty()) emailError = ""
                        },
                        label = "Email",
                        error = emailError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field
                    AgribuzTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordStrength = calculatePasswordStrength(it)
                            if (passwordError.isNotEmpty()) passwordError = ""
                        },
                        label = "Password",
                        leadingIcon = passwordIcon,
                        isPassword = true,
                        error = passwordError,
                        showStrength = true,
                        strength = passwordStrength,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password field
                    AgribuzTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            if (confirmPasswordError.isNotEmpty()) confirmPasswordError = ""
                        },
                        label = "Confirm Password",
                        leadingIcon = passwordIcon,
                        isPassword = true,
                        error = confirmPasswordError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Signup button
                    Button(
                        onClick = {
                            showErrors = true
                            //validate the fields
                            validateForm(
                                displayName, email, password, confirmPassword,
                                { nameErr -> nameError = nameErr }, //nameError = it
                                { emailErr -> emailError = emailErr },
                                { passErr -> passwordError = passErr },
                                { confirmErr -> confirmPasswordError = confirmErr },
                                { valid -> isFormValid = valid }
                            )
                            if (isFormValid) viewModel.signUp(email, password, displayName)
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
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                            ) else Text(
                            "Register",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                    }

                    if (authState is AuthUiState.Error) {

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = (authState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Already have an account link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(
                            onClick = onNavigateToLogin,
                        ) {
                            Text(
                                text = "Log In",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Terms & Conditions text at bottom
            Text(
                text = buildAnnotatedString {
                    append("By signing up, you agree to our ")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )) {
                        append("Terms of Service")
                    }
                    append(" and ")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )) {
                        append("Privacy Policy")
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            )

            // Farming footer image
            Image(
                painter = painterResource(id = R.drawable.crops_potatos), // Replace with your farm crops image
                contentDescription = "Crops",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 32.dp),
                contentScale = ContentScale.FillWidth,
                alpha = 0.7f
            )

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

// Helper function to calculate password strength (0-4)
private fun calculatePasswordStrength(password: String): Int {
    if (password.isEmpty()) return 0

    var score = 0

    // Length check
    if (password.length >= 8) score++

    // Contains uppercase
    if (password.any { it.isUpperCase() }) score++

    // Contains lowercase
    if (password.any { it.isLowerCase() }) score++

    // Contains digit
    if (password.any { it.isDigit() }) score++

    // Contains special character
    if (password.any { !it.isLetterOrDigit() }) score++

    return minOf(score, 4)
}

/** Map score to a user‐friendly label. */
private fun passwordStrengthLabel(score: Int): String = when (score) {
    0, 1 -> "Very weak"
    2    -> "Weak"
    3    -> "Medium"
    4    -> "Strong"
    5    -> "Very strong"
    else -> ""
}

// Validation function
private fun validateForm(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    setNameError: (String) -> Unit,
    setEmailError: (String) -> Unit,
    setPasswordError: (String) -> Unit,
    setConfirmPasswordError: (String) -> Unit,
    setFormValid: (Boolean) -> Unit
) {
    var isValid = true

    // Name validation
    if (name.trim().isEmpty()) {
        setNameError("Name is required")
        isValid = false
    } else if (name.trim().length < 3) {
        setNameError("Name must be at least 3 characters")
        isValid = false
    } else {
        setNameError("")
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

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
    } else if (password.length < 8) {
        setPasswordError("Password must be at least 8 characters")
        isValid = false
    } else if (calculatePasswordStrength(password) < 3) {
        setPasswordError("Password is too weak")
        isValid = false
    } else {
        setPasswordError("")
    }

    // Confirm password validation
    if (confirmPassword.isEmpty()) {
        setConfirmPasswordError("Please confirm your password")
        isValid = false
    } else if (confirmPassword != password) {
        setConfirmPasswordError("Passwords don't match")
        isValid = false
    } else {
        setConfirmPasswordError("")
    }

    setFormValid(isValid)
}


//// Simple phone validation
//private fun isValidPhoneNumber(phone: String): Boolean {
//    // Basic validation: at least 10 digits
//    val digitsOnly = phone.filter { it.isDigit() }
//    return digitsOnly.length >= 10
//}
//
//fun formatToE164(localNumber: String): String {
//    val cleaned = localNumber.replace("[^\\d]".toRegex(), "")
//    return if (cleaned.startsWith("0")) {
//        "+254${cleaned.substring(1)}"
//    } else if (cleaned.startsWith("254")) {
//        "+$cleaned"
//    } else if (!cleaned.startsWith("+")) {
//        "+254$cleaned"
//    } else {
//        cleaned
//    }
//}
//
//private fun formatPhoneNumber(input: String): String {
//    val trimmed = input.trim().replace(" ", "").replace("-", "")
//    return when {
//        trimmed.startsWith("+") -> trimmed
//        trimmed.startsWith("0") -> "+254" + trimmed.drop(1)
//        trimmed.length == 9 && trimmed.startsWith("7") -> "+254$trimmed"
//        else -> trimmed // fallback; optionally return error here
//    }
//}


