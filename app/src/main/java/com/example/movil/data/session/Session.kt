// Session.kt — guarda EN MEMORIA y en DataStore los datos de la sesión activa.
// Usa DataStore para persistir el token entre reinicios de la app.

package com.example.movil.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

// DataStore para guardar preferencias
val Context.dataStore by preferencesDataStore("settings")

object Session {
    var token: String? = null        // token que devolvió el backend al iniciar sesión
    var userName: String? = null     // nombre del usuario logueado (para saludarlo)
    var isAdmin: Boolean = false     // ¿el usuario tiene el rol Administrador?

    private val TOKEN_KEY = stringPreferencesKey("token")

    // Devuelve el encabezado Authorization listo para Retrofit
    fun bearer(): String = "Bearer $token"

    // Guardar token en DataStore y en memoria
    suspend fun saveToken(context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
        this.token = token
    }

    // Cargar token desde DataStore
    suspend fun loadToken(context: Context): String? {
        val prefs = context.dataStore.data.first()
        val savedToken = prefs[TOKEN_KEY]
        this.token = savedToken
        return savedToken
    }

    // Borrar sesión (memoria + DataStore)
    suspend fun clear(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
        token = null
        userName = null
        isAdmin = false
    }
}
