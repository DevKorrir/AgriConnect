package dev.korryr.agrimarket.ui.features.auth.phoneAuth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.auth.data.rrepo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: FirebaseUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun register(phoneNumber: String, password: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repo.register(phoneNumber, password).collect { result ->
                if (result.isSuccess) {
                    _uiState.value = AuthUiState.Success(result.getOrThrow())
                } else {
                    _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            }
        }
    }
}