package com.example.movil.data.orders

import com.google.gson.annotations.SerializedName

data class CheckoutRequest(
    @SerializedName("payment_method") val paymentMethod: String
)

data class CheckoutResponse(
    val message: String, val order: Order
)

data class OrdersResponse(val orders: List<Order> = emptyList())

data class OrderResponse(val order: Order)

data class Order(
    val id: Int,
    @SerializedName("user_id") val userId: Int?,
    val status: String,
    @SerializedName("payment_method") val paymentMethod: String,
    val total: Double,
    @SerializedName("created_at") val createdAt: String,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val id: Int,
    @SerializedName("book_id") val bookId: Int,
    val title: String,
    val quantity: Int,
    @SerializedName("unit_price") val unitPrice: Double,
    val subtotal: Double
)

enum class PaymentMethod(val apiValue: String, val label: String) {
    CARD("tarjeta", "Tarjeta"), PSE("pse", "PSE"), CASH(
        "efectivo",
        "Efectivo"
    ),
    TRANSFER("transferencia", "Transferencia")
}
