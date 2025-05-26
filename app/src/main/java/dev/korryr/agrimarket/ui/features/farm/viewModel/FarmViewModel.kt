package dev.korryr.agrimarket.ui.features.farm.viewModel

import android.content.Context
import android.net.Uri
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

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage

    init {
        // As soon as ViewModel is created, try to load existing farm
        auth.currentUser?.uid?.let { loadFarm(it) }
    }

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
                //save to firestore
                repo.saveFarm(farm)

                // load the saved farm from firestore
                val savedFarm = repo.getFarmByOwner(ownerUid)

                _uiState.value = FarmProfileUiState.Success(savedFarm)
                //_uiState.value = FarmProfileUiState.Saving(false)
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

    fun uploadProfileImage(
        uri: Uri,
        context: Context
    ) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            try {
                val imageUrl = uploadImageToStorage(uri, context)
                updateProfileImageUrl(imageUrl)
            } catch (e: Exception) {
                _uiState.value =
                    FarmProfileUiState.Error(e.localizedMessage ?: "Image upload failed")
            } finally {
                _isUploadingImage.value = false
            }
        }
    }

    private suspend fun uploadImageToStorage(
        uri: Uri,
        context: Context
    ): String {
        return repo.uploadImageToStorage(uri, context)
    }

    private suspend fun updateProfileImage(imageUrl: String) {
        val currentState = _uiState.value
        if (currentState is FarmProfileUiState.Success) {
            val updatedProfile = currentState.profile?.copy(imageUrl = imageUrl)
            _uiState.value = FarmProfileUiState.Success(updatedProfile)

            // Save to Firestore if profile exists
            updatedProfile?.let { repo.saveFarm(it) }
        }
    }

//    private suspend fun uploadImageToStorage(uri: Uri, context: Context): String {
//        return withContext(Dispatchers.IO) {
//            val storage = Firebase.storage
//            val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
//            val imageRef = storage.reference.child("farm_profiles/$userId/profile_${System.currentTimeMillis()}.jpg")
//
//            val uploadTask = imageRef.putFile(uri).await()
//            imageRef.downloadUrl.await().toString()
//        }
//    }
//
//    private suspend fun updateProfileImage(imageUrl: String) {
//        val currentState = _uiState.value
//        if (currentState is FarmProfileUiState.Success) {
//            val updatedProfile = currentState.profile?.copy(imageUrl = imageUrl)
//            _uiState.value = FarmProfileUiState.Success(updatedProfile)
//
//            // Save to Firestore if profile exists
//            updatedProfile?.let { repo.saveFarm(it) }
//        }
//    }


}