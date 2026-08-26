package com.example.movil.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object Profile : Routes("profile")
    object EditProfile : Routes("edit_profile")
    object ChangePassword : Routes("change_password")
    object AdminUsers : Routes("admin_users")
    object CreateAdmin : Routes("create_admin")

    object Books : Routes("books")
    object BookDetail : Routes("book/{bookId}") {
        fun create(bookId: Int) = "book/$bookId"
    }

    object Cart : Routes("cart")
    object Checkout : Routes("checkout")
    object Orders : Routes("orders")
    object OrderDetail : Routes("order/{orderId}") {
        fun create(orderId: Int) = "order/$orderId"
    }
}
