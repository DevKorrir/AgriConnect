package dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.auth.data.rrepo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import dev.korryr.agrimarket.ui.features.auth.preferences.AuthPreferencesRepository

/**
 * Represents a user signing up with email/password.
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val firestore: FirebaseFirestore,
    private val preferenceRepository: AuthPreferencesRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState

    /**
     * Register a new user via repository, then save to Firestore.
     */
    fun signUp(email: String, password: String, displayName: String) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _authState.value = AuthUiState.Error("Please fill all fields")
            return
        }

        _authState.value = AuthUiState.Loading
        viewModelScope.launch {
            repo.signUp(email, password).collect { result ->
                result.fold(onSuccess = { user ->
                    // Update display name
                    user.updateProfile(
                        com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()
                    )?.addOnCompleteListener { upd ->
                        if (upd.isSuccessful) saveUserToFirestore(user)
                        else _authState.value = AuthUiState.Error("Profile update failed: ${upd.exception?.message}")
                    }
                }, onFailure = {
                    _authState.value = AuthUiState.Error(it.message ?: "Sign-up failed")
                })
            }
        }
    }

    /**
     * Log in an existing user via repository.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("Please provide email and password")
            return
        }

        _authState.value = AuthUiState.Loading
        viewModelScope.launch {
            repo.login(email, password).collect { result ->
                result.fold(onSuccess = { user ->
                    val uid = user.uid
                    //save to preference
                    preferenceRepository.setLoggedIn(uid)
                    _authState.value = AuthUiState.Success(user)
                }, onFailure = {
                    _authState.value = AuthUiState.Error(it.message ?: "Login failed")
                })
            }
        }
    }

    /**
     * Save authenticated user's profile to Firestore.
     */
    private fun saveUserToFirestore(user: FirebaseUser) {
        val data = mapOf(
            "uid" to user.uid,
            "email" to user.email,
            "displayName" to (user.displayName ?: ""),
            "createdAt" to FieldValue.serverTimestamp()
        )
        firestore.collection("users").document(user.uid)
            .set(data)
            .addOnSuccessListener {
                _authState.value = AuthUiState.Success(user)
            }
            .addOnFailureListener { e ->
                _authState.value = AuthUiState.Error("Firestore error: ${e.localizedMessage}")
            }
    }

//    private fun registerFarmer(uid: String, farmer: Farmer) {
//        val data = mapOf(
//            "name" to farmer.name,
//            "phone" to farmer.phone,
//            "verified" to true,
//            "uid" to uid,
//            "createdAt" to FieldValue.serverTimestamp()
//        )
//        firestore.collection("farmers").document(uid)
//            .set(data)
//            .addOnSuccessListener {
//                _phoneState.value = PhoneAuthState.Registered("Registration successful")
//            }
//            .addOnFailureListener { e ->
//                _phoneState.value = PhoneAuthState.Error("Firestore error: ${e.localizedMessage}")
//            }
//    }
//
//    // Format helper (e.g. E.164)
//    private fun formatToE164(localNumber: String): String {
//        val cleaned = localNumber.replace("[^\\d]".toRegex(), "")
//        return when {
//            cleaned.startsWith("0") -> "+254${cleaned.drop(1)}"
//            cleaned.startsWith("254") -> "+$cleaned"
//            cleaned.startsWith("+") -> cleaned
//            cleaned.length == 9 -> "+254$cleaned"
//            else -> cleaned
//        }
//    }




}