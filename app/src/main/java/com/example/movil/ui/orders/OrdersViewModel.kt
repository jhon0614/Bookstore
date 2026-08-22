package com.example.movil.ui.orders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movil.data.cart.DataResult
import com.example.movil.data.orders.OrdersRepository
import com.example.movil.data.orders.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OrdersRepository(application)
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    fun checkout(method: PaymentMethod) {
        viewModelScope.launch {
            beginLoading()
            when (val result = repository.checkout(method)) {
                is DataResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false, confirmedOrder = result.data.order
                )
                is DataResult.Error -> showError(result)
            }
        }
    }

    fun loadOrders() {
        viewModelScope.launch {
            beginLoading()
            when (val result = repository.getOrders()) {
                is DataResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false, orders = result.data.orders
                )
                is DataResult.Error -> showError(result)
            }
        }
    }

    fun loadOrder(orderId: Int) {
        viewModelScope.launch {
            beginLoading()
            when (val result = repository.getOrder(orderId)) {
                is DataResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false, selectedOrder = result.data.order
                )
                is DataResult.Error -> showError(result)
            }
        }
    }

    fun consumeConfirmation() {
        _uiState.value = _uiState.value.copy(confirmedOrder = null)
    }

    private fun beginLoading() {
        _uiState.value = _uiState.value.copy(
            isLoading = true, error = null, unauthorized = false, stockConflict = false
        )
    }

    private fun showError(error: DataResult.Error) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = error.message,
            unauthorized = error.isUnauthorized,
            stockConflict = error.statusCode == 409
        )
    }
}
