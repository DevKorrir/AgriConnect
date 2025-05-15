package dev.korryr.agrimarket.ui.features.auth.phoneAuth.register

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField
import dev.korryr.agrimarket.R

@Composable
fun SignupScreen(
    onSignupComplete: () -> Unit, onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(

                text = "Sign Up", style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onSignupComplete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Register")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBack) {
                Text(text = "Back")
            }
        }
    }
}



@Composable
fun AgribuzSignupScreen(
    onSignupClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    // State variables
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    // Validation states
    var nameError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    // Password strength (0-4)
    var passwordStrength by remember { mutableStateOf(0) }

    // Form validity
    var isFormValid by remember { mutableStateOf(false) }

    // Icons
    val userIcon = painterResource(id = R.drawable.user) // Replace with your user icon
    val phoneIcon = painterResource(id = R.drawable.phone_call) // Replace with your phone icon
    val passwordIcon = painterResource(id = R.drawable.padlock)

    // Calculate password strength
    LaunchedEffect(password) {
        passwordStrength = calculatePasswordStrength(password)
        validateForm(
            name, phone, password, confirmPassword,
            { nameErr -> nameError = nameErr },
            { phoneErr -> phoneError = phoneErr },
            { passErr -> passwordError = passErr },
            { confirmErr -> confirmPasswordError = confirmErr },
            { valid -> isFormValid = valid }
        )
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
                        modifier = Modifier.size(40.dp)
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
                        text = "Join our farming community",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Name field
                    AgribuzTextField(
                        value = name,
                        onValueChange = {
                            name = it
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
                        value = phone,
                        onValueChange = {
                            phone = it
                            if (phoneError.isNotEmpty()) phoneError = ""
                        },
                        label = "Phone Number",
                        leadingIcon = phoneIcon,
                        error = phoneError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
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
                        onDone = {
                            if (isFormValid) onSignupClick()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Signup button
                    Button(
                        onClick = onSignupClick,
                        enabled = isFormValid,
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
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
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

                        TextButton(onClick = onLoginClick) {
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

// Validation function
private fun validateForm(
    name: String,
    phone: String,
    password: String,
    confirmPassword: String,
    setNameError: (String) -> Unit,
    setPhoneError: (String) -> Unit,
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

    // Phone validation
    if (phone.trim().isEmpty()) {
        setPhoneError("Phone number is required")
        isValid = false
    } else if (!isValidPhoneNumber(phone)) {
        setPhoneError("Enter a valid phone number")
        isValid = false
    } else {
        setPhoneError("")
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

// Simple phone validation
private fun isValidPhoneNumber(phone: String): Boolean {
    // Basic validation: at least 10 digits
    val digitsOnly = phone.filter { it.isDigit() }
    return digitsOnly.length >= 10
}

@Preview(showBackground = true)
@Composable
fun AgribuzSignupScreenPreview() {
    MaterialTheme {
        AgribuzSignupScreen()
    }
}