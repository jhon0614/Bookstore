package com.example.movil.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    isAdmin: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToBooks: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("BookStore+", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Historias para cada momento",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📚", style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Tu próxima lectura está aquí", style = MaterialTheme.typography.titleLarge)
                        Text("Busca por autor, género, título o ISBN")
                    }
                }
            }
        }

        item {
            HomeOption(Icons.AutoMirrored.Filled.MenuBook, "Explorar libros", "Descubre y filtra el catálogo", onNavigateToBooks)
        }
        item {
            HomeOption(Icons.Default.ShoppingCart, "Mi carrito", "Revisa los libros seleccionados", onNavigateToCart)
        }
        item {
            HomeOption(Icons.AutoMirrored.Filled.ReceiptLong, "Mis pedidos", "Consulta compras y detalles", onNavigateToOrders)
        }
        if (isLoggedIn) {
            item { HomeOption(Icons.Default.Person, "Mi perfil", "Actualiza tus datos y contraseña", onNavigateToProfile) }
        } else {
            item {
                OutlinedButton(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Iniciar sesión")
                }
                Text(
                    "También puedes comprar como invitado",
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (isAdmin) {
            item {
                Text(
                    "Administración",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            item { HomeOption(Icons.Default.AdminPanelSettings, "Gestionar usuarios", "Consulta usuarios y crea administradores", onNavigateToUsers) }
        }
    }
}

@Composable
private fun HomeOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}
