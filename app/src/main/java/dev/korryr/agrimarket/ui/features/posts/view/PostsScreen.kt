package dev.korryr.agrimarket.ui.features.posts.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import dev.korryr.agrimarket.ui.features.posts.viewModel.FarmPostViewModel
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField
import dev.korryr.agrimarket.ui.theme.FertileEarthBrown
import dev.korryr.agrimarket.ui.theme.White

@Composable
fun CreatePostScreen(
    postViewModel: FarmPostViewModel = hiltViewModel(),
    onPostSuccess: () -> Unit,
    navConctroller: NavController
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }

    var descriptionError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }
    var quantityError by remember { mutableStateOf("") }
    var sizeError by remember { mutableStateOf("") }
    var imageError by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
        if (uri == null) {
            imageError = "Please select an image"
        }
    }

    val isPosting by postViewModel.isPosting.collectAsState()
    val error by postViewModel.error.collectAsState()
    val isPostSuccessful by postViewModel.isPostSuccessful.collectAsState()

    // Function to clear all fields
    fun clearAllFields() {
        imageUri = null
        description = ""
        price = ""
        quantity = ""
        size = ""
        descriptionError = ""
        priceError = ""
        quantityError = ""
        sizeError = ""
        imageError = ""
    }

    val colorScheme = MaterialTheme.colorScheme


    // Check if form is complete for button state
    val isFormComplete = imageUri != null &&
            description.isNotBlank() && description.length >= 10 &&
            price.isNotBlank() && price.toDoubleOrNull() != null && price.toDoubleOrNull()!! > 0 &&
            quantity.isNotBlank() && quantity.toIntOrNull() != null && quantity.toIntOrNull()!! > 0 &&
            size.isNotBlank() && size.length >= 2

// Animation for form appearance
    val animatedVisibility = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedVisibility.animateTo(1f, tween(800, easing = EaseOutCubic))
    }

    // Handle post success
    LaunchedEffect(isPostSuccessful) {
        if (isPostSuccessful) {
            clearAllFields()
            onPostSuccess()
            // Reset the success state in ViewModel to prevent repeated clearing
            postViewModel.resetPostSuccessState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorScheme.background, colorScheme.surface),
                    startY = 0f,
                    endY = 600f
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            //hearder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .clip(
                            CircleShape
                        )
                        .clickable {
                            navConctroller.navigateUp()
                        }
                        .size(28.dp)
                        .background(
                            colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .padding(6.dp)

                )

                Spacer(modifier = Modifier.width(24.dp))

                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .padding(6.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    "Create New Post",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                )
            }

            // Image Upload Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        //overlay for selecting
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    )
                                    .padding(8.dp)
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = if (imageError.isNotEmpty()) colorScheme.error else colorScheme.primary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        if (imageError.isNotEmpty())
                                            colorScheme.error.copy(alpha = 0.1f)
                                        else
                                            colorScheme.primary.copy(alpha = 0.1f),
                                        CircleShape
                                    )
                                    .padding(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "Add Product Photo",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (imageError.isNotEmpty()) colorScheme.error else colorScheme.primary
                                )
                            )
                            Text(
                                "Tap to select from gallery",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = colorScheme.onSurfaceVariant
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Image error message
            AnimatedVisibility(
                visible = imageError.isNotEmpty(),
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Text(
                    text = imageError,
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }

            // Form Fields
            AnimatedVisibility(
                visible = animatedVisibility.value > 0.5f,
                enter = slideInVertically() + fadeIn()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Description Field
                    AgribuzTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            if (it.isNotBlank() && descriptionError.isNotEmpty()) {
                                descriptionError =
                                    if (it.length < 10) "Description must be at least 10 characters" else ""
                            }
                        },
                        label = "Product Description",
                        hint = "Describe your product in detail...",
                        error = descriptionError,
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        )
                    )

                    // Price Field
                    AgribuzTextField(
                        value = price,
                        onValueChange = {
                            price = it
                            if (it.isNotBlank() && priceError.isNotEmpty()) {
                                val priceValue = it.toDoubleOrNull()
                                priceError =
                                    if (priceValue == null || priceValue <= 0) "Please enter a valid price" else ""
                            }
                        },
                        label = "Price (KES)",
                        hint = "0.00",
                        error = priceError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        //modifier = Modifier.weight(1f)
                    )

                    // Quantity Field
                    AgribuzTextField(
                        value = quantity,
                        onValueChange = {
                            quantity = it
                            if (it.isNotBlank() && quantityError.isNotEmpty()) {
                                val quantityValue = it.toIntOrNull()
                                quantityError =
                                    if (quantityValue == null || quantityValue <= 0) "Please enter a valid quantity" else ""
                            }
                        },
                        label = "Quantity",
                        hint = "e.g., 50 kg",
                        error = quantityError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        //modifier = Modifier.weight(1f)
                    )


                    // Farm Size Field
                    AgribuzTextField(
                        value = size,
                        onValueChange = {
                            size = it
                            if (it.isNotBlank() && sizeError.isNotEmpty()) {
                                sizeError =
                                    if (it.length < 2) "Please provide more details about farm size" else ""
                            }
                        },
                        label = "Farm Size",
                        hint = "e.g., 2 acres, 500 sqm",
                        error = sizeError,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            // Submit Button
            AnimatedVisibility(
                visible = animatedVisibility.value > 0.7f,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = EaseOutBounce)
                ) + fadeIn()
            ) {

                Button(
                    onClick = {
                        showErrors = true
                        val isValid = validateForm(
                            imageUri, description, price, quantity, size,
                            { imageError = it },
                            { descriptionError = it },
                            { priceError = it },
                            { quantityError = it },
                            { sizeError = it }
                        )

                        if (isValid) {
                            // 3a. Reset the ViewModel’s success state (so that the LaunchedEffect below can react once more)
                            postViewModel.resetPostSuccessState()

                            // 3b. Create a new post (imageUri is non-null here because validation passed)
                            val imageUrl = imageUri.toString() ?: ""
                            postViewModel.createPost(
                                imageUrl = imageUrl,
                                description = description,
                                price = price.toDoubleOrNull() ?: 0.0,
                                quantity = quantity.toIntOrNull() ?: 0,
                                size = size
                            )

                        }
                    },
                    enabled = !isPosting && isFormComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            if (isFormComplete) 12.dp else 4.dp,
                            RoundedCornerShape(28.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = White,
                        disabledContainerColor = FertileEarthBrown.copy(alpha = 0.5f),
                    ),
                    shape = RoundedCornerShape(28.dp)

                ) {
                    if (isPosting) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                "Publishing...",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = colorScheme.onPrimary
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Publish,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (isFormComplete) colorScheme.onPrimary else colorScheme.onSurface.copy(
                                    alpha = 0.38f
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isFormComplete) "Publish Post" else "Complete Form to Publish",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (isFormComplete) colorScheme.onPrimary else colorScheme.onSurface.copy(
                                    alpha = 0.38f
                                )
                            )
                        }
                    }
                }
            }


//            if (error != null) {
//                Text(error ?: "", color = Color.Red)
//            }

            // Error Message
            AnimatedVisibility(
                visible = error != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            error ?: "",
                            color = colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Form validation function
private fun validateForm(
    imageUri: Uri?,
    description: String,
    price: String,
    quantity: String,
    size: String,
    imageError: (String) -> Unit,
    descriptionError: (String) -> Unit,
    priceError: (String) -> Unit,
    quantityError: (String) -> Unit,
    sizeError: (String) -> Unit
): Boolean {
    var isValid = true

    // Validate image
    if (imageUri == null) {
        imageError("Please select a product image")
        isValid = false
    } else {
        imageError("")
    }

    // Validate description
    if (description.isEmpty()) {
        descriptionError("Product description is required")
        isValid = false
    } else if (description.length < 10) {
        descriptionError("Description must be at least 10 characters")
        isValid = false
    } else {
        descriptionError("")
    }

    // Validate price
    val priceValue = price.toDoubleOrNull()
    if (price.isBlank()) {
        priceError("Price is required")
        isValid = false
    } else if (priceValue == null || priceValue <= 0) {
        priceError("Please enter a valid price")
        isValid = false
    } else {
        priceError("")
    }

    // Validate quantity
    val quantityValue = quantity.toIntOrNull()
    if (quantity.isBlank()) {
        quantityError("Quantity is required")
        isValid = false
    } else if (quantityValue == null || quantityValue <= 0) {
        quantityError("Please enter a valid quantity")
        isValid = false
    } else {
        quantityError("")
    }

    // Validate size
    if (size.isBlank()) {
        sizeError("Farm size is required")
        isValid = false
    } else if (size.length < 2) {
        sizeError("Please provide more details about farm size")
        isValid = false
    } else {
        sizeError("")
    }

    return isValid
}



