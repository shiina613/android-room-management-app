package com.kma.lamphoun.roomapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("access_token")
        private val KEY_ROLE = stringPreferencesKey("role")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_FULL_NAME = stringPreferencesKey("full_name")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val role: Flow<String?> = context.dataStore.data.map { it[KEY_ROLE] }
    val userId: Flow<Long?> = context.dataStore.data.map { it[KEY_USER_ID] }
    val fullName: Flow<String?> = context.dataStore.data.map { it[KEY_FULL_NAME] }
    val isLoggedIn: Flow<Boolean> = token.map { !it.isNullOrBlank() }

    suspend fun save(token: String, role: String, userId: Long, fullName: String) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_ROLE] = role
            it[KEY_USER_ID] = userId
            it[KEY_FULL_NAME] = fullName
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}

