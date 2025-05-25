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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.farm.presentation.FarmProfileHeader
import dev.korryr.agrimarket.ui.features.farm.presentation.FarmingTypeSelector
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileUiState
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileViewModel
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField

enum class FarmScreenMode {
    LOADING,
    CREATE,
    VIEW_AND_POST
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmProfileScreen(
    farmViewModel: FarmProfileViewModel = hiltViewModel(),
    onBackPressed: () -> Unit = {},
    onNavigateToPostScreen: () -> Unit = {}
) {
    val uiState by farmViewModel.uiState.collectAsState()
    val isSaved by farmViewModel.isSaved.collectAsState()
    val state = uiState

    var isEditMode by remember { mutableStateOf(false) }

//    // Determine if farmer has existing profile
//    val hasExistingFarm = when (
//        val currentState = uiState
//    ) {
//        is FarmProfileUiState.Success -> currentState.profile != null
//        else -> false
//    }

    // Determine if there’s an existing farm
    val hasExistingFarm = (uiState as? FarmProfileUiState.Success)?.profile != null

//    // Handle save success
//    LaunchedEffect(isSaved) {
//        if (isSaved) {
//            isEditMode = false
//            farmViewModel.resetSavedFlag()
//        }
//    }

    // Switch off edit mode once saved
    LaunchedEffect(isSaved) {
        if (isSaved) {
            farmViewModel.resetSavedFlag()
        }
    }


//    val profile = (uiState as? FarmProfileUiState.Success)?.profile
//    val state = uiState
//
//    // Form state
//    var farmName by rememberSaveable { mutableStateOf("") }
//    var location by rememberSaveable { mutableStateOf("") }
//    var farmingType by rememberSaveable { mutableStateOf("") }
//    var contact by rememberSaveable { mutableStateOf("") }
//    var showErrors by rememberSaveable { mutableStateOf(false) }
//
//    // Error states
//    var farmNameError by remember { mutableStateOf("") }
//    var locationError by remember { mutableStateOf("") }
//    var contactError by remember { mutableStateOf("") }
//
//    val isLoading = uiState is FarmProfileUiState.Saving
//    val errorMessage = (uiState as? FarmProfileUiState.Error)?.message
//
//    // Farming type options
//    val farmingTypes = listOf(
//        "Crop Farming",
//        "Livestock",
//        "Poultry",
//        "Horticulture",
//        "Mixed Farming",
//        "Organic Farming",
//        "Aquaculture",
//        "Dairy Farming"
//    )
//
//    var screenMode by remember { mutableStateOf(FarmScreenMode.CREATE) }
//
//    LaunchedEffect(uiState) {
//        when (val currentState = uiState) {
//            is FarmProfileUiState.Success -> {
//                currentState.profile?.let { profile ->
//                    farmName = profile.farmName
//                    location = profile.location
//                    farmingType = profile.typeOfFarming ?: ""
//                    contact = profile.contact
//                    screenMode = FarmScreenMode.VIEW_AND_POST
//                } ?: run {
//                    screenMode = FarmScreenMode.CREATE
//                }
//            }
//
//            is FarmProfileUiState.Loading -> {
//                screenMode = FarmScreenMode.LOADING
//            }
//
//            is FarmProfileUiState.Error -> {
//                screenMode = FarmScreenMode.CREATE
//            }
//        }
//    }
//
//    LaunchedEffect(isSaved) {
//        if (isSaved) {
//            screenMode = FarmScreenMode.VIEW_AND_POST
//            farmViewModel.resetSavedFlag()
//        }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            uiState is FarmProfileUiState.Loading -> "Loading..."
                            hasExistingFarm && !isEditMode -> "My Farm Dashboard"
                            hasExistingFarm && isEditMode -> "Edit Farm Profile"
                            else -> "Create Farm Profile"
                        },
                        fontWeight = FontWeight.Medium
                    )
                },

                actions = {
                    if (hasExistingFarm && !isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Farm"
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (hasExistingFarm && !isEditMode) {
                FloatingActionButton(
                    onClick = onNavigateToPostScreen
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Post"
                    )
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState is FarmProfileUiState.Loading -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }

            hasExistingFarm && !isEditMode -> {
                FarmDashboardContent(
                    modifier = Modifier.padding(paddingValues),
                    profile = (uiState as FarmProfileUiState.Success).profile!!,
                    onEdit = { isEditMode = true }
                )
            }

            else -> {
                CreateEditFarmContent(
                    modifier = Modifier.padding(paddingValues),
                    uiState = uiState,
                    farmViewModel = farmViewModel,
                    isEditMode = isEditMode,
                    existingProfile = if (hasExistingFarm) (uiState as FarmProfileUiState.Success).profile else null,
                    onCancelEdit = { isEditMode = false }
                )
            }
        }
    }
}


//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Farm Profile Image
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Image(
//                    painter = rememberAsyncImagePainter(
//                        profile?.imageUrl.orEmpty().ifEmpty { "https://via.placeholder.com/150" }),
//                    contentDescription = "Farm Profile Image",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(100.dp)
//                        .clip(CircleShape)
//                        .clickable { /* TODO: open image picker */ }
//                )
//            }
//
//
//            // Header Section
//            FarmProfileHeader()
//
//            when (screenMode) {
//                FarmScreenMode.LOADING -> {
//                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//                }
//                FarmScreenMode.CREATE -> {
//                    FarmProfileHeader()
//                    CreateFarmProfileContent(
//                        farmName = farmName,
//                        onFarmNameChange = {
//                            farmName = it; farmNameError = ""
//                        },
//                        location = location,
//                        onLocationChange = {
//                            location = it; locationError = ""
//                        },
//                        farmingType = farmingType,
//                        onFarmingTypeSelected = { farmingType = it },
//                        contact = contact,
//                        onContactChange = {
//                            contact = it; contactError = ""
//                        },
//                        farmingTypes = farmingTypes,
//                        farmNameError = farmNameError,
//                        locationError = locationError,
//                        contactError = contactError,
//                        isLoading = isLoading,
//                        onSaveClick = {
//                            showErrors = true
//                            validateAndSave(
//                                farmName = farmName,
//                                location = location,
//                                farmingType = farmingType,
//                                contact = contact,
//                                setFarmNameError = { farmNameError = it ?: "" },
//                                setLocationError = { locationError = it ?: "" },
//                                setContactError = { contactError = it ?: "" },
//                                onValidationSuccess = {
//                                    farmViewModel.saveFarmProfile(
//                                        farmName, location, farmingType, contact
//                                    )
//                                }
//                            )
//                        }
//                    )
//                }
//                FarmScreenMode.VIEW_AND_POST -> {
//                    FarmDashboardContent(
//                        farmName = farmName,
//                        location = location,
//                        farmingType = farmingType,
//                        contact = contact
//                    )
//                }
//            }
//
//            if (state is FarmProfileUiState.Error) {
//                Text(
//                    text = state.message,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
//    }
//}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading your farm profile...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun FarmDashboardContent(
    modifier: Modifier = Modifier,
    profile: FarmProfile,
    onEdit: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Farm Profile Image
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    profile.imageUrl?.ifEmpty { "https://via.placeholder.com/150" }
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

        // Farm Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = profile.farmName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = profile.location,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = profile.typeOfFarming ?: "Not specified",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = profile.contact,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Farm Statistics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Farm Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        label = "Posts",
                        value = "0" // TODO: Get from actual data
                    )
                    StatisticItem(
                        label = "Followers",
                        value = "0" // TODO: Get from actual data
                    )
                    StatisticItem(
                        label = "Following",
                        value = "0" // TODO: Get from actual data
                    )
                }
            }
        }

        // Recent Posts Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Posts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { /* TODO: Navigate to all posts */ }) {
                        Text("View All")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Placeholder for no posts
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "No posts yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Start sharing your farm produce!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CreateEditFarmContent(
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