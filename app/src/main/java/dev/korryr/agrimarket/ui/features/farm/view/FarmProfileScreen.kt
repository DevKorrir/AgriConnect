package dev.korryr.agrimarket.ui.features.farm.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileUiState
import dev.korryr.agrimarket.ui.features.farm.viewModel.FarmProfileViewModel

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
    val scaffold = PaddingValues()

    var isEditMode by remember { mutableStateOf(false) }

    // Determine if farmer has existing profile
    val hasExistingFarm = when (
        val currentState = uiState
    ) {
        is FarmProfileUiState.Success -> currentState.profile != null
        else -> false
    }    // Determine if there’s an existing farm
    // val hasExistingFarm = (uiState as? FarmProfileUiState.Success)?.profile != null


    // Handle save success and switch off edit mode
    LaunchedEffect(isSaved) {
        if (isSaved) {
            isEditMode = false
            farmViewModel.resetSavedFlag()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
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
//        floatingActionButton = {
//            if (hasExistingFarm && !isEditMode) {
//                FloatingActionButton(
//                    onClick = onNavigateToPostScreen,
//                    containerColor = MaterialTheme.colorScheme.primary
//                ) {
//                    Row(
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
//                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
//                    ) {
//                        Icon(
//                            Icons.Default.Add,
//                            contentDescription = "Add Post"
//                        )
//                        Text(
//                            text = "Add Post",
//                            fontWeight = FontWeight.Bold,
//                            color = MaterialTheme.colorScheme.onPrimary
//                        )
//                    }
//                }
//            }
//        }

            when (uiState) {
                is FarmProfileUiState.Loading -> LoadingScreen(Modifier.fillMaxSize())
                is FarmProfileUiState.Success -> {
                    if (hasExistingFarm && !isEditMode) {
                        FarmDashboardContent(
                            profile = (uiState as FarmProfileUiState.Success).profile!!,
                            modifier = Modifier.fillMaxSize(),
                            onEdit = { isEditMode = true }
                        )
                    } else {
                        CreateEditFarmContent(
                            modifier = Modifier.fillMaxSize(),
                            uiState = uiState,
                            farmViewModel = farmViewModel,
                            isEditMode = isEditMode,
                            existingProfile = if (hasExistingFarm) (uiState as FarmProfileUiState.Success).profile else null,
                            onCancelEdit = { isEditMode = false }
                        )
                    }
                }

                is FarmProfileUiState.Error -> {
                    // show error + fallback to create form
                    CreateEditFarmContent(
                        modifier = Modifier.fillMaxSize(),
                        uiState = uiState,
                        farmViewModel = farmViewModel,
                        isEditMode = isEditMode,
                        existingProfile = null,
                        onCancelEdit = { isEditMode = false }
                    )
                }

                else -> {}
            }
        }

        // Floating Action Button positioned at bottom end
        if (hasExistingFarm && !isEditMode) {
            FloatingActionButton(
                onClick = onNavigateToPostScreen,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        8.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Post"
                    )
                    Text(
                        text = "Add Post",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

}