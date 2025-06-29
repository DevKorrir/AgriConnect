package dev.korryr.agrimarket.ui.features.postManagement.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.korryr.agrimarket.ui.features.postManagement.viewModel.ManagePostViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostDialog(
    modifier: Modifier = Modifier,
    post: FarmPost,
    onDismiss: () -> Unit,
    onSave: (FarmPost) -> Unit,
    managePostViewModel: ManagePostViewModel = hiltViewModel()
) {
    // Form states
    var description by remember { mutableStateOf(post.description) }
    var price by remember { mutableStateOf(post.price.toString()) }
    var quantity by remember { mutableStateOf(post.quantity.toString()) }
    var unit by remember { mutableStateOf(post.unit ?: "") }
    var farmSize by remember { mutableStateOf(post.size ?: "") }
    var imageUrl by remember { mutableStateOf(post.imageUrl) }
    var showImagePicker by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }

    val isUpdating by managePostViewModel.isUpdating.collectAsState()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isUploadingImage = true
            managePostViewModel.uploadNewImage(it.toString()) { result ->
                result.fold(
                    onSuccess = { newImageUrl ->
                        imageUrl = newImageUrl
                        isUploadingImage = false
                    },
                    onFailure = {
                        isUploadingImage = false
                        // Handle error
                    }
                )
            }
        }
    }

    val units = listOf("kg", "tonnes", "pieces", "bags", "boxes", "bundles")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Post",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }

                // Image Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Product Image",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { showImagePicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isUploadingImage -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Uploading...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            imageUrl.isNotBlank() -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(imageUrl),
                                        contentDescription = "Product Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Edit overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Change Image",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }

                            else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoLibrary,
                                        contentDescription = "Add Image",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Tap to add image",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Price and Quantity Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price (KES)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = {
                            Text(
                                text = "KES",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                // Unit and Farm Size Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    var expandedUnit by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expandedUnit,
                        onExpandedChange = { expandedUnit = !expandedUnit },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnit) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedUnit,
                            onDismissRequest = { expandedUnit = false }
                        ) {
                            units.forEach { unitOption ->
                                DropdownMenuItem(
                                    text = { Text(unitOption) },
                                    onClick = {
                                        unit = unitOption
                                        expandedUnit = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = farmSize,
                        onValueChange = { farmSize = it },
                        label = { Text("Farm Size (optional)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val priceValue = price.toDoubleOrNull() ?: 0.0
                            val updatedPost = post.copy(
                                description = description.trim(),
                                price = priceValue,
                                quantity = quantity.toIntOrNull()
                                    ?: 0,  // Convert quantity back to Int (or appropriate type)
                                unit = unit.trim(),
                                size = farmSize,
                                imageUrl = imageUrl
                            )
                            onSave(updatedPost)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isUpdating && description.isNotBlank() && price.isNotBlank() && quantity.isNotBlank()
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }

// Image picker dialog
    if (showImagePicker) {
        AlertDialog(
            onDismissRequest = { showImagePicker = false },
            title = { Text("Choose Image Source") },
            text = { Text("Select how you want to add an image") },
            confirmButton = {
                TextButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                        showImagePicker = false
                    }
                ) {
                    Text("Gallery")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImagePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
