package com.example.movil.ui.cart

import com.example.movil.data.cart.CartResponse

data class CartUiState(
    val isLoading: Boolean = false,
    val cart: CartResponse = CartResponse(),
    val error: String? = null,
    val message: String? = null,
    val unauthorized: Boolean = false
)
