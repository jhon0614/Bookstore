package com.example.movil.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movil.data.cart.CartItem
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    onUnauthorized: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var confirmClear by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadCart() }
    LaunchedEffect(state.unauthorized) { if (state.unauthorized) onUnauthorized() }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Mi carrito") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            })
        }
    ) { padding ->
        Column(Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {
            when {
                state.isLoading && state.cart.items.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                state.error != null && state.cart.items.isEmpty() -> ErrorContent(
                    state.error!!,
                    viewModel::loadCart
                )

                state.cart.items.isEmpty() -> Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío")
                }

                else -> {
                    LazyColumn(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.cart.items, key = { it.id }) { item ->
                            CartItemCard(
                                item = item,
                                enabled = !state.isLoading,
                                onQuantity = {
                                    viewModel.updateQuantity(
                                        item.id,
                                        it,
                                        item.book.stock
                                    )
                                },
                                onRemove = { viewModel.removeItem(item.id) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Resumen", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${state.cart.count} ejemplares",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                money(state.cart.total),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text("Continuar con el pago")
                    }
                    TextButton(
                        onClick = { confirmClear = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text("Vaciar carrito")
                    }
                }
            }
            state.message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            state.error?.takeIf { state.cart.items.isNotEmpty() }?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (confirmClear) AlertDialog(
        onDismissRequest = { confirmClear = false },
        title = { Text("Vaciar carrito") },
        text = { Text("¿Deseas quitar todos los libros del carrito?") },
        confirmButton = {
            TextButton(onClick = {
                confirmClear = false; viewModel.clearCart()
            }) { Text("Vaciar") }
        },
        dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("Cancelar") } }
    )
}

@Composable
private fun CartItemCard(
    item: CartItem,
    enabled: Boolean,
    onQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(item.book.title, style = MaterialTheme.typography.titleMedium)
            Text(
                item.book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(money(item.book.price), color = MaterialTheme.colorScheme.primary)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { onQuantity(item.quantity - 1) },
                    enabled = enabled && item.quantity > 1
                ) { Text("−") }
                Text(item.quantity.toString(), modifier = Modifier.padding(horizontal = 16.dp))
                OutlinedButton(
                    onClick = { onQuantity(item.quantity + 1) },
                    enabled = enabled && item.quantity < item.book.stock
                ) { Text("+") }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onRemove, enabled = enabled) {
                    Icon(Icons.Default.Delete, contentDescription = "Quitar libro")
                }
            }
            Text("Subtotal: ${money(item.subtotal)}", style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun ErrorContent(message: String, retry: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        Button(onClick = retry) { Text("Reintentar") }
    }
}

internal fun money(value: Double): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO")).format(value)
