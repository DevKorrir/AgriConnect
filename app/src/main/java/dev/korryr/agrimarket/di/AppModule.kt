package dev.korryr.agrimarket.di

//import jakarta.inject.Singleton
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.korryr.agrimarket.netObserver.ConnectivityObserver
import dev.korryr.agrimarket.netObserver.NetworkConnectivityObserver
import dev.korryr.agrimarket.ui.features.auth.data.remote.AuthService
import dev.korryr.agrimarket.ui.features.auth.data.remote.FirebaseAuthService
import dev.korryr.agrimarket.ui.features.auth.data.rrepo.AuthRepository
import dev.korryr.agrimarket.ui.features.auth.preferences.AuthPreferencesRepository
import dev.korryr.agrimarket.ui.features.postManagement.model.FarmPostServiceImpl
import dev.korryr.agrimarket.ui.features.postManagement.model.FarmPostsService
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
        authService: AuthService,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepository(
        authService,
        firestore
    )

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthPreferencesRepository(
        @ApplicationContext context: Context // inject apllication context
    ): AuthPreferencesRepository {
        return AuthPreferencesRepository(context) // pass context to the constructor
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideFarmPostsService(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        auth: FirebaseAuth
    ): FarmPostsService {
        return FarmPostServiceImpl(firestore, storage)
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext ctx: Context): ConnectivityObserver =
        NetworkConnectivityObserver(ctx)



}

