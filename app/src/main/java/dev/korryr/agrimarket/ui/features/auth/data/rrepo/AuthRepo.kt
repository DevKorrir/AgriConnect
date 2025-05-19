package dev.korryr.agrimarket.ui.features.auth.data.rrepo

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dev.korryr.agrimarket.ui.features.auth.data.remote.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository layer for email/password authentication.
 */
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val firestore: FirebaseFirestore
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
    suspend fun login(
        email: String,
        password: String
    ): Result<FirebaseUser> =
        authService.login(email, password)


    /** new: fetch the stored role for a uid */
    suspend fun fetchRole(
        uid: String
    ): Result<String> = try {
        val snap = firestore.collection("users")
            .document(uid)
            .get()
            .await()
        Result.success(snap.getString("role") ?: "USER")
    } catch(e: Exception) {
        Result.failure(e)
    }






}
