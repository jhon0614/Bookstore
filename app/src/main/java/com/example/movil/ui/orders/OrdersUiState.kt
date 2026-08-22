package com.example.movil.ui.orders

import com.example.movil.data.orders.Order

data class OrdersUiState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val selectedOrder: Order? = null,
    val confirmedOrder: Order? = null,
    val error: String? = null,
    val unauthorized: Boolean = false,
    val stockConflict: Boolean = false
)
