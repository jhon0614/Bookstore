package com.example.movil.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil.data.cart.CartRepository
import com.example.movil.data.cart.CartResponse
import com.example.movil.data.cart.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CartRepository(application)
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() = runRequest { repository.getCart() }

    fun addToCart(bookId: Int, quantity: Int) {
        if (quantity <= 0) {
            _uiState.value = _uiState.value.copy(error = "La cantidad debe ser mayor que cero")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)
            when (val result = repository.addItem(bookId, quantity)) {
                is DataResult.Success -> {
                    _uiState.value =
                        _uiState.value.copy(message = result.data.message ?: "Libro agregado")
                    loadCart()
                }

                is DataResult.Error -> showError(result)
            }
        }
    }

    fun updateQuantity(itemId: Int, quantity: Int, stock: Int) {
        if (quantity !in 1..stock) {
            _uiState.value = _uiState.value.copy(
                error = if (quantity < 1) "La cantidad mínima es 1" else "Solo hay $stock ejemplares disponibles"
            )
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)
            when (val result = repository.updateItem(itemId, quantity)) {
                is DataResult.Success -> loadCart()
                is DataResult.Error -> showError(result)
            }
        }
    }

    fun removeItem(itemId: Int) = mutate { repository.removeItem(itemId) }
    fun clearCart() = mutate { repository.clearCart() }

    fun consumeMessage() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    private fun runRequest(request: suspend () -> DataResult<CartResponse>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = request()) {
                is DataResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false, cart = result.data, unauthorized = false
                )

                is DataResult.Error -> showError(result)
            }
        }
    }

    private fun mutate(request: suspend () -> DataResult<*>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, message = null)
            when (val result = request()) {
                is DataResult.Success -> loadCart()
                is DataResult.Error -> showError(result)
            }
        }
    }

    private fun showError(error: DataResult.Error) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = error.message,
            unauthorized = error.isUnauthorized
        )
    }
}
