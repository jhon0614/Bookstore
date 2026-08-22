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

    // Placeholders para tus compañeros de equipo
    object Books : Routes("books")
    object Cart : Routes("cart")
    object Orders : Routes("orders")
}
