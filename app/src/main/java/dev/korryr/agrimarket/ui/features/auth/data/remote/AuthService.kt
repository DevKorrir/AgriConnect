package dev.korryr.agrimarket.ui.features.auth.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthService {
    fun signUp(
        phoneNumber: String,
        password: String,
    ): Flow<Result<FirebaseUser>>
}


class FirebaseAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthService {
    override fun signUp(phoneNumber: String, password: String): Flow<Result<FirebaseUser>> = flow {
        emit(Result.failure(Throwable("LOADING")))
        try {
            // Placeholder logic: Firebase does not support sign-up with phone and password directly.
            // You would typically implement phone auth with verification code.
            // For now, simulate with email-like logic for structure.
            val result = firebaseAuth
                .createUserWithEmailAndPassword("$phoneNumber@agrimarket.com", password)
                .await()
            result.user?.let { emit(Result.success(it)) }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
