package com.example.movil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.Session
import com.example.movil.navigation.Routes
import com.example.movil.ui.admin.UsersScreen
import com.example.movil.ui.auth.LoginScreen
import com.example.movil.ui.auth.RegisterScreen
import com.example.movil.ui.home.HomeScreen
import com.example.movil.ui.profile.ChangePasswordScreen
import com.example.movil.ui.profile.EditProfileScreen
import com.example.movil.ui.profile.ProfileScreen
import com.example.movil.ui.theme.MovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    // Al iniciar, validamos el token guardado y restauramos toda la sesión.
                    LaunchedEffect(Unit) {
                        val savedToken = Session.loadToken(context)
                        if (!savedToken.isNullOrEmpty()) {
                            try {
                                val response = RetrofitClient.api.getProfile(Session.bearer())
                                val user = response.body()?.user
                                if (response.isSuccessful && user != null) {
                                    Session.userName = user.UserName
                                    Session.isAdmin = user.roles?.any {
                                        it.TypeRole == "Administrador"
                                    } == true
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                } else {
                                    Session.clear(context)
                                }
                            } catch (_: Exception) {
                                // Conservamos el token ante un fallo temporal de red.
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Routes.LOGIN
                    ) {
                        composable(Routes.LOGIN) { LoginScreen(navController) }
                        composable(Routes.REGISTER) { RegisterScreen(navController) }
                        composable(Routes.HOME) { HomeScreen(navController) }
                        composable(Routes.EDIT_PROFILE) { EditProfileScreen(navController) }
                        composable(Routes.PROFILE) { ProfileScreen(navController) }
                        composable(Routes.CHANGE_PASSWORD) { ChangePasswordScreen(navController) }
                        composable(Routes.USERS) { UsersScreen(navController) }
                    }
                }
            }
        }
    }
}
