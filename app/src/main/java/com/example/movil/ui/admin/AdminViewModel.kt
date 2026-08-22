package com.example.movil.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil.data.model.CreateAdminRequest
import com.example.movil.data.model.UserProfile
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.ui.auth.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.getApiService(application)

    private val _usersState = MutableStateFlow<UiState<List<UserProfile>>>(UiState.Idle)
    val usersState: StateFlow<UiState<List<UserProfile>>> = _usersState

    private val _createAdminState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val createAdminState: StateFlow<UiState<String>> = _createAdminState

    fun getUsers(query: String = "") {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            try {
                val res = if (query.isEmpty()) api.getUsers() else api.searchUsers(query)
                if (res.isSuccessful) {
                    val list = if (query.isEmpty()) {
                        (res.body() as? com.example.movil.data.model.UserPageResponse)?.users ?: emptyList()
                    } else {
                        (res.body() as? List<*>)?.filterIsInstance<UserProfile>() ?: emptyList()
                    }

                    if (list.isEmpty()) {
                        _usersState.value = UiState.Empty
                    } else {
                        _usersState.value = UiState.Success(list)
                    }
                } else {
                    _usersState.value = UiState.Error("Error listando usuarios: ${res.code()}")
                }
            } catch (e: Exception) {
                _usersState.value = UiState.Error(e.localizedMessage ?: "Error en la consulta")
            }
        }
    }

    fun createAdmin(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _createAdminState.value = UiState.Loading
            try {
                val res = api.createAdmin(CreateAdminRequest(userName = name, email = email, passwordKey = pass))
                if (res.isSuccessful) {
                    _createAdminState.value = UiState.Success("Administrador creado correctamente")
                } else {
                    _createAdminState.value = UiState.Error("Error creando administrador (${res.code()})")
                }
            } catch (e: Exception) {
                _createAdminState.value = UiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            try {
                val res = api.deleteUser(id)
                if (res.isSuccessful) getUsers()
            } catch (_: Exception) {}
        }
    }

    fun resetCreateState() { _createAdminState.value = UiState.Idle }
}

