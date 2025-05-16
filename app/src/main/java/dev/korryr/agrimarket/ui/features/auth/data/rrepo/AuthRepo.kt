package dev.korryr.agrimarket.ui.features.auth.data.rrepo


import com.google.firebase.auth.FirebaseUser
import dev.korryr.agrimarket.ui.features.auth.data.remote.AuthService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    fun register(
        phoneNumber: String, password: String
    ): Flow<Result<FirebaseUser>> = authService.signUp(phoneNumber, password)
}