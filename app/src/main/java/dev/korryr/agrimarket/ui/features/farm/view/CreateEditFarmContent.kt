package dev.korryr.agrimarket.ui.features.farm.view

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.farm.presentation.FarmProfileHeader
import dev.korryr.agrimarket.ui.features.farm.presentation.FarmingTypeSelector
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileUiState
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileViewModel
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField

@Composable
fun CreateEditFarmContent(
    modifier: Modifier = Modifier,
    uiState: FarmProfileUiState,
    farmViewModel: FarmProfileViewModel,
    isEditMode: Boolean,
    existingProfile: FarmProfile?,
    onCancelEdit: () -> Unit
) {
    // Form state
    var farmName by rememberSaveable { mutableStateOf(existingProfile?.farmName ?: "") }
    var location by rememberSaveable { mutableStateOf(existingProfile?.location ?: "") }
    var farmingType by rememberSaveable { mutableStateOf(existingProfile?.typeOfFarming ?: "") }
    var contact by rememberSaveable { mutableStateOf(existingProfile?.contact ?: "") }

    // Error states
    var farmNameError by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }
    var contactError by remember { mutableStateOf("") }

    val isLoading = uiState is FarmProfileUiState.Saving

    // Farming type options
    val farmingTypes = listOf(
        "Crop Farming",
        "Livestock",
        "Poultry",
        "Horticulture",
        "Mixed Farming",
        "Organic Farming",
        "Aquaculture",
        "Dairy Farming"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Profile Image Section (for edit mode)
        if (isEditMode) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        existingProfile?.imageUrl?.ifEmpty { "https://via.placeholder.com/150" }
                            ?: "https://via.placeholder.com/150"
                    ),
                    contentDescription = "Farm Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { /* TODO: open image picker */ }
                )
            }
        } else {
            // Header for create mode
            FarmProfileHeader()
        }

        // Form Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Farm Name Field
                AgribuzTextField(
                    value = farmName,
                    onValueChange = {
                        farmName = it
                        farmNameError = ""
                    },
                    label = "Farm Name",
                    hint = "Enter your farm name",
                    error = farmNameError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                // Location Field
                AgribuzTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        locationError = ""
                    },
                    label = "Farm Location",
                    hint = "Enter farm location",
                    error = locationError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )

                // Farming Type Section
                FarmingTypeSelector(
                    selectedType = farmingType,
                    onTypeSelected = { farmingType = it },
                    farmingTypes = farmingTypes
                )

                // Contact Field
                AgribuzTextField(
                    value = contact,
                    onValueChange = {
                        contact = it
                        contactError = ""
                    },
                    label = "Contact Information",
                    hint = "0712345678",
                    error = contactError,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }

        // Error Message
        if (uiState is FarmProfileUiState.Error) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isEditMode) Arrangement.spacedBy(8.dp) else Arrangement.Center
        ) {
            if (isEditMode) {
                OutlinedButton(
                    onClick = onCancelEdit,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }
            }

            Button(
                onClick = {
                    validateAndSave(
                        farmName = farmName,
                        location = location,
                        farmingType = farmingType,
                        contact = contact,
                        setFarmNameError = { farmNameError = it ?: "" },
                        setLocationError = { locationError = it ?: "" },
                        setContactError = { contactError = it ?: "" },
                        onValidationSuccess = {
                            farmViewModel.saveFarmProfile(
                                farmName = farmName,
                                location = location,
                                typeOfFarming = farmingType,
                                contactInfo = contact
                            )
                        }
                    )
                },
                modifier = if (isEditMode) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saving...")
                } else {
                    Text(
                        text = if (isEditMode) "Update Farm" else "Create Farm"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


private fun validateAndSave(
    farmName: String,
    location: String,
    farmingType: String,
    contact: String,
    setFarmNameError: (String?) -> Unit,
    setLocationError: (String?) -> Unit,
    setContactError: (String?) -> Unit,
    onValidationSuccess: () -> Unit
) {
    var isValid = true

    // Validate farm name
    when {
        farmName.isBlank() -> {
            setFarmNameError("Farm name is required")
            isValid = false
        }

        farmName.length < 3 -> {
            setFarmNameError("Farm name must be at least 3 characters")
            isValid = false
        }

        else -> setFarmNameError(null)
    }

    // Validate location
    if (location.isBlank()) {
        setLocationError("Location is required")
        isValid = false
    } else {
        setLocationError(null)
    }

    // Validate contact
    when {
        contact.isBlank() -> {
            setContactError("Contact information is required")
            isValid = false
        }

        contact.length < 10 -> {
            setContactError("Please enter a valid contact number")
            isValid = false
        }

        else -> setContactError(null)
    }

    if (isValid) {
        onValidationSuccess()
    }
}