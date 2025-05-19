package dev.korryr.agrimarket.ui.features.auth.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("auth_preferences")

@Singleton
class AuthPreferencesRepository @Inject constructor (context: Context){
    private val dataStore = context.dataStore

    companion object {
        private val KEY_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun setLoggedIn(userId: String) {
        dataStore.edit { preferences ->
            preferences[KEY_LOGGED_IN] = true
            preferences[KEY_USER_ID] = userId
        }
    }

    suspend fun setLoggedOut() {
        dataStore.edit { preferences ->
            preferences[KEY_LOGGED_IN] = false
            preferences.remove(KEY_USER_ID)
        }
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_LOGGED_IN] ?: false
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_USER_ID]
    }



}