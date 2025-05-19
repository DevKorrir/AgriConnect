package dev.korryr.agrimarket.ui.features.auth.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Service interface for email/password authentication.
 */
interface AuthService {
    /**
     * Creates a new user with email & password.
     * @return the created FirebaseUser
     */
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>

    /**
     * Signs in an existing user with email & password.
     * @return the authenticated FirebaseUser
     */
    suspend fun login(email: String, password: String): Result<FirebaseUser>
}

/**
 * Firebase implementation of AuthService using email/password.
 */
class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth
) : AuthService {

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) Result.success(user)
            else Result.failure(Throwable("User creation failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) Result.success(user)
            else Result.failure(Throwable("Authentication failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
