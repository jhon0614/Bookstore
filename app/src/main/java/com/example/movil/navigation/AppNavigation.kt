package com.example.movil.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movil.data.books.BooksRepository
import com.example.movil.data.remote.RetrofitClient
import com.example.movil.data.session.SessionManager
import com.example.movil.ui.admin.AdminViewModel
import com.example.movil.ui.admin.CreateAdminScreen
import com.example.movil.ui.admin.UsersScreen
import com.example.movil.ui.auth.AuthViewModel
import com.example.movil.ui.auth.LoginScreen
import com.example.movil.ui.auth.RegisterScreen
import com.example.movil.ui.books.BookDetailScreen
import com.example.movil.ui.books.BookDetailViewModel
import com.example.movil.ui.books.BooksScreen
import com.example.movil.ui.books.BooksViewModel
import com.example.movil.ui.cart.CartScreen
import com.example.movil.ui.cart.CartViewModel
import com.example.movil.ui.cart.CheckoutScreen
import com.example.movil.ui.home.HomeScreen
import com.example.movil.ui.orders.OrderDetailScreen
import com.example.movil.ui.orders.OrdersScreen
import com.example.movil.ui.orders.OrdersViewModel
import com.example.movil.ui.profile.ChangePasswordScreen
import com.example.movil.ui.profile.EditProfileScreen
import com.example.movil.ui.profile.ProfileScreen
import com.example.movil.ui.profile.ProfileViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember(context) { SessionManager(context) }
    val session by sessionManager.session.collectAsState(initial = null)

    if (session == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val authViewModel: AuthViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val ordersViewModel: OrdersViewModel = viewModel()
    val booksRepository = remember(context) { BooksRepository(RetrofitClient.getBooksApiService(context)) }
    val booksViewModel: BooksViewModel = viewModel(factory = BooksViewModel.provideFactory(booksRepository))

    val loggedIn = !session?.token.isNullOrBlank()
    val isAdmin = session?.role == "admin"
    val startDestination = if (loggedIn) Routes.Home.route else Routes.Login.route
    val goToLogin = { navController.navigate(Routes.Login.route) { popUpTo(0) } }

    LaunchedEffect(loggedIn) {
        if (!loggedIn && navController.currentDestination?.route != Routes.Login.route) goToLogin()
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Login.route) {
            LoginScreen(authViewModel, { navController.navigate(Routes.Home.route) { popUpTo(0) } },
                { navController.navigate(Routes.Register.route) })
        }
        composable(Routes.Register.route) {
            RegisterScreen(authViewModel, { navController.navigate(Routes.Home.route) { popUpTo(0) } },
                { navController.popBackStack() })
        }
        composable(Routes.Home.route) {
            HomeScreen(
                isAdmin = isAdmin,
                onNavigateToProfile = { navController.navigate(Routes.Profile.route) },
                onNavigateToUsers = { navController.navigate(Routes.AdminUsers.route) },
                onNavigateToBooks = { navController.navigate(Routes.Books.route) },
                onNavigateToCart = { navController.navigate(Routes.Cart.route) },
                onNavigateToOrders = { navController.navigate(Routes.Orders.route) }
            )
        }
        composable(Routes.Profile.route) {
            ProfileScreen(profileViewModel, { navController.navigate(Routes.EditProfile.route) },
                { navController.navigate(Routes.ChangePassword.route) }, goToLogin)
        }
        composable(Routes.EditProfile.route) { EditProfileScreen(profileViewModel) { navController.popBackStack() } }
        composable(Routes.ChangePassword.route) { ChangePasswordScreen(profileViewModel) { navController.popBackStack() } }
        composable(Routes.AdminUsers.route) {
            if (isAdmin) UsersScreen(adminViewModel) { navController.navigate(Routes.CreateAdmin.route) }
        }
        composable(Routes.CreateAdmin.route) {
            if (isAdmin) CreateAdminScreen(adminViewModel) { navController.popBackStack() }
        }
        composable(Routes.Books.route) {
            BooksScreen(
                viewModel = booksViewModel,
                onBookClick = { navController.navigate(Routes.BookDetail.create(it)) }
            )
        }
        composable(
            Routes.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { entry ->
            val bookId = entry.arguments?.getInt("bookId") ?: return@composable
            val detailViewModel: BookDetailViewModel = viewModel(
                key = "book-$bookId",
                factory = BookDetailViewModel.provideFactory(booksRepository, bookId)
            )
            BookDetailScreen(
                viewModel = detailViewModel,
                onAddToCart = { id, quantity ->
                    cartViewModel.addToCart(id, quantity)
                    navController.navigate(Routes.Cart.route)
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.Cart.route) {
            CartScreen({ navController.popBackStack() }, { navController.navigate(Routes.Checkout.route) }, goToLogin, cartViewModel)
        }
        composable(Routes.Checkout.route) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onOrderConfirmed = { navController.navigate(Routes.OrderDetail.create(it)) { popUpTo(Routes.Cart.route) { inclusive = true } } },
                onUnauthorized = goToLogin,
                cartViewModel = cartViewModel,
                ordersViewModel = ordersViewModel
            )
        }
        composable(Routes.Orders.route) {
            OrdersScreen({ navController.popBackStack() }, { navController.navigate(Routes.OrderDetail.create(it)) }, goToLogin, ordersViewModel)
        }
        composable(
            Routes.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { entry ->
            val orderId = entry.arguments?.getInt("orderId") ?: return@composable
            OrderDetailScreen(orderId, { navController.popBackStack() }, goToLogin, ordersViewModel)
        }
    }
}
