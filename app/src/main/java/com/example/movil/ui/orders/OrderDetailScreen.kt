package com.example.movil.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movil.ui.cart.money
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun OrderDetailScreen(
    orderId: Int,
    onBack: () -> Unit,
    onUnauthorized: () -> Unit,
    viewModel: OrdersViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }
    LaunchedEffect(state.unauthorized) { if (state.unauthorized) onUnauthorized() }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Detalle del pedido") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            })
        }
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadOrder(orderId) }) { Text("Reintentar") }
                }

                state.selectedOrder == null -> Text(
                    "Pedido no encontrado",
                    Modifier.align(Alignment.Center)
                )

                else -> {
                    val order = state.selectedOrder!!
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            Text(
                                "Pedido #${order.id}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text("Estado: ${order.status.replaceFirstChar { it.uppercase() }}")
                            Text("Fecha: ${readableDate(order.createdAt)}")
                            Text("Método de pago: ${order.paymentMethod.replaceFirstChar { it.uppercase() }}")
                            Spacer(Modifier.height(12.dp))
                        }
                        items(order.items, key = { it.id }) { item ->
                            ElevatedCard(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Cantidad: ${item.quantity}")
                                    Text("Precio unitario: ${money(item.unitPrice)}")
                                    Text("Subtotal: ${money(item.subtotal)}")
                                }
                            }
                        }
                        item {
                            HorizontalDivider(Modifier.padding(vertical = 12.dp))
                            Text(
                                "Total: ${money(order.total)}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
