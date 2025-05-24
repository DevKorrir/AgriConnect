package dev.korryr.agrimarket.ui.features.farm.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    var farmName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var farmingType by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
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
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
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

@Composable
private fun FarmProfileHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.size(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Farm Information",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Please provide your farm details",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FarmingTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    farmingTypes: List<String>
) {
    Column {
        Text(
            text = "Type of Farming",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
                    .padding(8.dp)
            ) {
                farmingTypes.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedType == type,
                                onClick = { onTypeSelected(type) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = null
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = type,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
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