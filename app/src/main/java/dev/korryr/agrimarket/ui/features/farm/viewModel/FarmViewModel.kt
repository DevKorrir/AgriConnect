package dev.korryr.agrimarket.ui.features.farm.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
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

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

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

    fun saveFarmProfile(
        farmName: String,
        location: String,
        typeOfFarming: String,
        contactInfo: String,
    ) {
        viewModelScope.launch {
            _uiState.value = FarmProfileUiState.Saving(true)
            try {
                val ownerUid = auth.currentUser?.uid ?: throw Exception("User not Logged in")
                val farm = FarmProfile(
                    farmId = ownerUid, //or you can generete uuid or use auto generfete key
                    ownerUid = ownerUid,
                    farmName = farmName,
                    location = location,
                    typeOfFarming = typeOfFarming,
                    contact = contactInfo
                )
                repo.saveFarm(farm)
                _uiState.value = FarmProfileUiState.Success(farm)
                _uiState.value = FarmProfileUiState.Saving(false)
                _isSaved.value = true // ✅ Trigger navigation
            } catch (e: Exception) {
                _uiState.value = FarmProfileUiState.Error(e.localizedMessage ?: "Save failed")
                _uiState.value = FarmProfileUiState.Saving(false)
            }
        }
    }

    fun resetSavedFlag() {
        _isSaved.value = false
    }


}