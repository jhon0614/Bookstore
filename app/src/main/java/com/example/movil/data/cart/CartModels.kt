package com.example.movil.data.cart

import com.google.gson.annotations.SerializedName

data class CartBookCategory(val id: Int, val name: String)

data class CartBook(
    val id: Int,
    val title: String,
    val author: String,
    val isbn: String,
    val genre: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    val available: Boolean,
    @SerializedName("cover_url") val coverUrl: String? = null,
    val categories: List<CartBookCategory> = emptyList()
)

data class CartItem(
    val id: Int, val book: CartBook, val quantity: Int, val subtotal: Double
)

data class CartResponse(
    val items: List<CartItem> = emptyList(), val count: Int = 0, val total: Double = 0.0
)

data class AddCartItemRequest(
    @SerializedName("book_id") val bookId: Int, val quantity: Int
)

data class UpdateCartItemRequest(val quantity: Int)

data class CartMutationResponse(
    val message: String? = null, val cart: CartResponse? = null
)

data class ApiMessage(val message: String? = null, val error: String? = null)

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val message: String, val statusCode: Int? = null) : DataResult<Nothing> {
        val isUnauthorized: Boolean get() = statusCode == 401
    }
}
