package dev.korryr.agrimarket.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.korryr.agrimarket.ui.features.auth.data.remote.AuthService
import dev.korryr.agrimarket.ui.features.auth.data.remote.FirebaseAuthService
import dev.korryr.agrimarket.ui.features.auth.data.rrepo.AuthRepository
//import jakarta.inject.Singleton
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        firebaseAuth: FirebaseAuth
    ): AuthService = FirebaseAuthService(firebaseAuth)

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService
    ): AuthRepository = AuthRepository(authService)
}

