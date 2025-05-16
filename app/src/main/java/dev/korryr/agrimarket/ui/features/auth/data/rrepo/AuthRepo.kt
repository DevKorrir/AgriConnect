package dev.korryr.agrimarket.ui.features.auth.data.rrepo

import com.google.firebase.auth.FirebaseUser
import dev.korryr.agrimarket.ui.features.auth.data.remote.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository layer for email/password authentication.
 */
class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    /**
     * Sign up a new user with email and password.
     * Returns a flow emitting the Result of FirebaseUser creation.
     */
    fun signUp(
        email: String,
        password: String
    ): Flow<Result<FirebaseUser>> = flow {
        emit(authService.signUp(email, password))
    }

    /**
     * Log in an existing user with email and password.
     * Returns a flow emitting the Result of FirebaseUser sign-in.
     */
    fun login(
        email: String,
        password: String
    ): Flow<Result<FirebaseUser>> = flow {
        emit(authService.login(email, password))
    }
}
