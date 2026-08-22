package com.example.movil.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movil.data.session.SessionManager
import com.example.movil.ui.admin.*
import com.example.movil.ui.auth.*
import com.example.movil.ui.home.HomeScreen
import com.example.movil.ui.profile.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    val token by sessionManager.authToken.collectAsState(initial = null)
    val role by sessionManager.userRole.collectAsState(initial = null)

    val startDestination = if (token.isNullOrEmpty()) Routes.Login.route else Routes.Home.route

    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Routes.Home.route) { popUpTo(0) } },
                onNavigateToRegister = { navController.navigate(Routes.Register.route) }
            )
        }
        composable(Routes.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.navigate(Routes.Home.route) { popUpTo(0) } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.Home.route) {
            HomeScreen(
                isAdmin = role == "admin",
                onNavigateToProfile = { navController.navigate(Routes.Profile.route) },
                onNavigateToUsers = { navController.navigate(Routes.AdminUsers.route) }
            )
        }
        composable(Routes.Profile.route) {
            ProfileScreen(
                viewModel = profileViewModel,
                onEditProfile = { navController.navigate(Routes.EditProfile.route) },
                onChangePassword = { navController.navigate(Routes.ChangePassword.route) },
                onLogout = { navController.navigate(Routes.Login.route) { popUpTo(0) } }
            )
        }
        composable(Routes.EditProfile.route) {
            EditProfileScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.ChangePassword.route) {
            ChangePasswordScreen(
                viewModel = profileViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.AdminUsers.route) {
            if (role == "admin") {
                UsersScreen(
                    viewModel = adminViewModel,
                    onCreateAdmin = { navController.navigate(Routes.CreateAdmin.route) }
                )
            } else {
                navController.popBackStack()
            }
        }
        composable(Routes.CreateAdmin.route) {
            if (role == "admin") {
                CreateAdminScreen(
                    viewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}