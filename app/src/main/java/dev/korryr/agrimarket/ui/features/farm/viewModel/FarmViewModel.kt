package dev.korryr.agrimarket.ui.features.farm.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.farm.data.repo.FarmRepository
import dev.korryr.agrimarket.ui.features.farm.data.repo.FarmRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FarmProfileUiState {
    object Loading : FarmProfileUiState()
    data class Success(val profile: FarmProfile?) : FarmProfileUiState()
    data class Saving(val isSaving: Boolean) : FarmProfileUiState()
    data class Error(val message: String) : FarmProfileUiState()
}

@HiltViewModel
class FarmProfileViewModel @Inject constructor(
    private val repo: FarmRepositoryImpl,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<FarmProfileUiState>(FarmProfileUiState.Loading)
    val uiState: StateFlow<FarmProfileUiState> = _uiState

    /** Load existing farm or null */
    fun loadFarm(ownerUid: String) {
        _uiState.value = FarmProfileUiState.Loading
        viewModelScope.launch {
            try {
                val profile = repo.getFarmByOwner(ownerUid)
                _uiState.value = FarmProfileUiState.Success(profile)
            } catch (e: Exception) {
                _uiState.value = FarmProfileUiState.Error("Load failed: ${e.localizedMessage}")
            }
        }
    }

    /** Create or update farm */
    fun saveFarm(
        ownerUid: String?,
        farmName: String,
        location: String,
        description: String,
        contact: String
    ) {
        _uiState.value = FarmProfileUiState.Saving(isSaving = true)
        viewModelScope.launch {
            try {
                    val id = ownerUid ?: auth.currentUser!!.uid
                    val farm = FarmProfile(id, farmName, location, description, contact)
                    repo.saveFarm(farm)   // repo handles create vs. update under-the-hood
                    _uiState.value = FarmProfileUiState.Success(farm)
                    _uiState.value = FarmProfileUiState.Saving(false)
                } catch (e: Exception) {
                _uiState.value = FarmProfileUiState.Error(e.localizedMessage ?: "Save failed")
            }

        }
    }
}