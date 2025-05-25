package dev.korryr.agrimarket.ui.features.farm.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.korryr.agrimarket.ui.features.farm.presentation.FarmProfileHeader
import dev.korryr.agrimarket.ui.features.farm.presentation.FarmingTypeSelector
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileUiState
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileViewModel
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmProfileScreen(
    farmViewModel: FarmProfileViewModel = hiltViewModel(),
    onSaved: () -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val uiState by farmViewModel.uiState.collectAsState()
    val isSaved by farmViewModel.isSaved.collectAsState()
    val state = uiState

    // Form state
    var farmName by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var farmingType by rememberSaveable { mutableStateOf("") }
    var contact by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

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

    // Populate fields when profile is loaded
    LaunchedEffect(uiState) {
        val currentUiState = uiState //capture for smart cast withiin lauch efffect
        if (currentUiState is FarmProfileUiState.Success) {
            currentUiState.profile?.let { profile ->
                farmName = profile.farmName
                location = profile.location
                farmingType = profile.typeOfFarming ?: ""
                contact = profile.contact
            }
        }
    }

    // Handle save success
    LaunchedEffect(isSaved) {
        if (isSaved) {
            onSaved()
            farmViewModel.resetSavedFlag()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val capturedUiState = uiState
                    Text(
                        text = if (capturedUiState is FarmProfileUiState.Success && capturedUiState.profile != null) {
                            "Edit Farm Profile"
                        } else {
                            "Create Farm Profile"
                        },
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header Section
            FarmProfileHeader()

            // Form Section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        hint = "Phone number or email",
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
            if (state is FarmProfileUiState.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Save Button
            Button(
                onClick = {
                    showErrors = true
                    validateAndSave(
                        farmName = farmName,
                        location = location,
                        farmingType = farmingType,
                        contact = contact,
                        setFarmNameError = { farmNameError = it ?: ""},
                        setLocationError = { locationError = it ?: ""},
                        setContactError = { contactError = it ?: ""},
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
                modifier = Modifier.fillMaxWidth(),
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
                        text = if (state is FarmProfileUiState.Success && state.profile != null) {
                            "Update Farm"
                        } else {
                            "Save Farm"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
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