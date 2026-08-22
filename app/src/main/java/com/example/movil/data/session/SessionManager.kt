package com.example.movil.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Renombrado a sessionDataStore para evitar conflicto de nombres a nivel de paquete
private val Context.sessionDataStore by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val ROLE_KEY = stringPreferencesKey("user_role")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    val authToken: Flow<String?> = context.sessionDataStore.data.map { pref -> pref[TOKEN_KEY] }
    val userRole: Flow<String?> = context.sessionDataStore.data.map { pref -> pref[ROLE_KEY] }
    val session: Flow<SessionData> = context.sessionDataStore.data.map { pref ->
        SessionData(pref[TOKEN_KEY], pref[ROLE_KEY])
    }

    suspend fun saveSession(token: String, role: String = "client", userId: String = "") {
        context.sessionDataStore.edit { pref ->
            pref[TOKEN_KEY] = token
            pref[ROLE_KEY] = role
            pref[USER_ID_KEY] = userId
        }
    }

    suspend fun getTokenSync(): String? {
        return context.sessionDataStore.data.map { pref -> pref[TOKEN_KEY] }.first()
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { pref -> pref.clear() }
    }
}

data class SessionData(val token: String?, val role: String?)
