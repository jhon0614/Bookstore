package com.example.movil.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil.data.model.LoginRequest
import com.example.movil.data.model.RegisterRequest
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    object Empty : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.getApiService(application)
    private val session = SessionManager(application)

    private val _authState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val authState: StateFlow<UiState<String>> = _authState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val res = api.login(LoginRequest(email, pass))
                if (res.isSuccessful && res.body() != null) {
                    val token = res.body()!!.token
                    val user = res.body()!!.user
                    val role = if (user.roles.any { it.name == "Administrador" }) "admin" else "client"
                    session.saveSession(token, role, user.id.toString())
                    _authState.value = UiState.Success("Login exitoso")
                } else {
                    _authState.value = UiState.Error("Credenciales inválidas (Err ${res.code()})")
                }
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val res = api.register(RegisterRequest(userName = name, email = email, passwordKey = pass))
                if (res.isSuccessful && res.body() != null) {
                    val token = res.body()!!.token
                    val user = res.body()!!.user
                    session.saveSession(token, "client", user.id.toString())
                    _authState.value = UiState.Success("Registro exitoso")
                } else {
                    _authState.value = UiState.Error("Falló el registro: ${res.code()}")
                }
            } catch (e: Exception) {
                _authState.value = UiState.Error(e.localizedMessage ?: "Error al conectar")
            }
        }
    }

    fun resetState() { _authState.value = UiState.Idle }
}

