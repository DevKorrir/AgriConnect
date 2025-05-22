package dev.korryr.agrimarket.ui.features.farm.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.korryr.agrimarket.R
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileUiState
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileViewModel
import dev.korryr.agrimarket.ui.shareUI.AgribuzTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmProfileScreen(
    farmViewModel: FarmProfileViewModel = hiltViewModel(),
    onSaved: () -> Unit = {},
) {
    val uiState by farmViewModel.uiState.collectAsState()

    // Local UI fields, initialized from state.profile when first loaded
    var ownerId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf("") }
    var locationError by remember { mutableStateOf("") }


    // Populate local fields when profile is loaded
    // Populate local fields when profile is loaded
    LaunchedEffect(uiState) {
        if (uiState is FarmProfileUiState.Success) {
            (uiState as FarmProfileUiState.Success).profile?.let { profile ->
                ownerId    = profile.ownerUid    // ← add this
                name       = profile.farmName
                location   = profile.location
                description= profile.description
                contact    = profile.contactInfo
            }
        }
    }

    // React to save success - This part might need adjustment based on how you signal save
    // For instance, you might introduce a new state like 'Saved' or use a separate Flow/State for it.
    // Let's assume for now your 'Saving' state eventually transitions to 'Success' or 'Error'.
    // Or perhaps you have a separate event/flag for "isSaved".
    // If FarmProfileViewModel updates uiState to Success after saving, this might be okay.
    // However, it's often better to have a more explicit signal for "saved".

    // For example, if 'Saving' state with isSaving=false means saved:
    LaunchedEffect(uiState) {
        if (uiState is FarmProfileUiState.Saving && !(uiState as FarmProfileUiState.Saving).isSaving) {
            // This is a guess. The condition for "isSaved" depends on your ViewModel logic.
            // A dedicated 'Saved' state or a separate Flow<Boolean> for save status is cleaner.
            onSaved()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // Adjust title based on whether a profile exists in the Success state
                        if (uiState is FarmProfileUiState.Success && (uiState as FarmProfileUiState.Success).profile != null) {
                            "Edit Farm"
                        } else {
                            "Create Farm"
                        }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.cabbage_backgroud),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            )
            Spacer(Modifier.height(24.dp))

            AgribuzTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (nameError.isNotEmpty()) nameError = ""
                },
                label = "Farm Name",
                hint = "Koromosho Farm",
                isPassword = false,
                showPassword = false,
                enabled = true,
                readOnly = false,
                error = nameError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
            )

            Spacer(Modifier.height(8.dp))

            AgribuzTextField(
                value = location,
                onValueChange = {
                    location = it
                    if (locationError.isNotEmpty()) locationError = ""
                },
                label = "Farm Location",
                hint = "Iten",
                enabled = true,
                error = locationError,
                modifier = Modifier.fillMaxSize(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
            )

//            OutlinedTextField(
//                value = location,
//                onValueChange = { location = it },
//                label = { Text("Location") },
//                isError = showErrors && location.isBlank(),
//                singleLine = true,
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
//                modifier = Modifier.fillMaxWidth()
//            )
           // if (showErrors && location.isBlank()) Text("Required", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                isError = showErrors && description.isBlank(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            if (showErrors && description.isBlank()) Text("Required", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact Info") },
                isError = showErrors && contact.isBlank(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
            if (showErrors && contact.isBlank()) Text("Required", color = MaterialTheme.colorScheme.error)

            var isFarmFormValid by remember { mutableStateOf(false) }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    showErrors = true
                    FarmValidation(
                        name, location, description, contact,
                        { nameErr -> nameError = nameErr },
                        { locErr -> location = locErr },
                        { descErr -> description = descErr },
                        { contErr -> contact = contErr },
                        setIsValid = { isValid -> isFarmFormValid = isValid },
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is FarmProfileUiState.Saving || !(uiState as FarmProfileUiState.Saving).isSaving
            ) {
                if (uiState is FarmProfileUiState.Saving && (uiState as FarmProfileUiState.Saving).isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Farm")
                }
            }

            if (uiState is FarmProfileUiState.Error) {
                Spacer(Modifier.height(8.dp))
                Text((uiState as FarmProfileUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun FarmValidation(
    name: String,
    location: String,
    description: String,
    contact: String,
    setNameError: (String) -> Unit,
    setLocationError: (String) -> Unit,
    setDescriptionError: (String) -> Unit,
    setContactError: (String) -> Unit,
    setIsValid: (Boolean) -> Unit
){
    var isValid = true

    //for famr name validation
    if (name.isBlank()) {
        setNameError("Name is required")
        isValid = false
    } else if (name.length < 3) {
        setNameError("too short")
        isValid = false
    } else {
        setNameError("")
    }

    //for location validation
    if (location.isBlank()) {
        setLocationError("Location is required")
        isValid = false
    } else {
        setLocationError("")
    }
}
