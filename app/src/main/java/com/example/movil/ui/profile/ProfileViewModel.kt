package com.example.movil.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil.data.model.ChangePasswordRequest
import com.example.movil.data.model.UserProfile
import com.example.movil.data.model.UpdateProfileRequest
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.SessionManager
import com.example.movil.ui.auth.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.getApiService(application)
    private val session = SessionManager(application)

    private val _profileState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)
    val profileState: StateFlow<UiState<UserProfile>> = _profileState

    private val _actionState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val actionState: StateFlow<UiState<String>> = _actionState

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            try {
                val res = api.getProfile()
                if (res.isSuccessful && res.body() != null) {
                    _profileState.value = UiState.Success(res.body()!!.user)
                } else {
                    _profileState.value = UiState.Error("Error al cargar perfil: ${res.code()}")
                }
            } catch (e: Exception) {
                _profileState.value = UiState.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    fun updateProfile(userName: String, email: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                val res = api.updateProfile(UpdateProfileRequest(userName, email))
                if (res.isSuccessful && res.body() != null) {
                    _profileState.value = UiState.Success(res.body()!!.user)
                    _actionState.value = UiState.Success("Perfil actualizado")
                } else {
                    _actionState.value = UiState.Error("Error al actualizar: ${res.code()}")
                }
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.localizedMessage ?: "Error de conexión")
            }
        }
    }

    fun changePassword(curr: String, newPass: String) {
        viewModelScope.launch {
            _actionState.value = UiState.Loading
            try {
                val res = api.changePassword(ChangePasswordRequest(curr, newPass))
                if (res.isSuccessful) {
                    _actionState.value = UiState.Success("Contraseña cambiada exitosamente")
                } else {
                    _actionState.value = UiState.Error("Contraseña actual incorrecta o formato inválido")
                }
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.localizedMessage ?: "Error al conectar")
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            session.clearSession()
            onComplete()
        }
    }

    fun resetActionState() { _actionState.value = UiState.Idle }
}

