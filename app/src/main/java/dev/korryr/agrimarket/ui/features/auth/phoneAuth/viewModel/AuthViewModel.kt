package dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.auth.data.rrepo.AuthRepository
import dev.korryr.agrimarket.ui.features.auth.preferences.AuthPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Represents a user signing up with email/password.
 */
sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser) : AuthUiState()
    data class SuccessWithRole(val user: FirebaseUser, val role: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val auth: FirebaseAuth,
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
     * Logs in the user and checks Firestore for 'role'.
     * Saves session to preferences and emits Admin or Success state.
     */
    fun login(email: String, password: String, isAdminLogin: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthUiState.Error("Please provide email and password")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthUiState.Loading

            repo.login(email, password)
                .fold(
                    onFailure = { _authState.value = AuthUiState.Error(it.message ?: "Login failed") },
                    onSuccess = { user ->
                        // 1) persist session
                        preferenceRepository.setLoggedIn(user.uid)

                        // 2) fetch role
                        repo.fetchRole(user.uid)
                            .fold(
                                onSuccess = { role ->
                                    // 3) enforce admin if required
                                    if (isAdminLogin && role != "ADMIN") {
                                        _authState.value = AuthUiState.Error("You are not an admin")
                                    } else {
                                        _authState.value = AuthUiState.SuccessWithRole(user, role)
                                    }
                                },
                                onFailure = { exception ->
                                    _authState.value = AuthUiState.Error(
                                        "Role lookup failed: ${exception.message}"
                                    )
                                }
                            )
                    }
                )
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
            "role" to "FARMER",
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
    fun logout() {

        // clear firebase session
        auth.signOut()

        // clear preferences
        viewModelScope.launch {
            preferenceRepository.setLoggedOut()
        }

        //reset locs=al ui state
        _authState.value = AuthUiState.Idle
    }




}