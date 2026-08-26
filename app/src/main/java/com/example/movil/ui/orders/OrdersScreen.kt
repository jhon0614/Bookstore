package com.example.movil.ui.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movil.data.orders.Order
import com.example.movil.ui.cart.money
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun OrdersScreen(
    onBack: () -> Unit,
    onOrderClick: (Int) -> Unit,
    onUnauthorized: () -> Unit,
    viewModel: OrdersViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadOrders() }
    LaunchedEffect(state.unauthorized) { if (state.unauthorized) onUnauthorized() }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Mis pedidos") }, navigationIcon = {
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
                    Button(onClick = viewModel::loadOrders) { Text("Reintentar") }
                }

                state.orders.isEmpty() -> Text(
                    "Todavía no tienes pedidos",
                    Modifier.align(Alignment.Center)
                )

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.orders, key = { it.id }) { order ->
                        OrderCard(order, onClick = { onOrderClick(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    ElevatedCard(Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pedido #${order.id}", style = MaterialTheme.typography.titleMedium)
                SuggestionChip(
                    onClick = {},
                    label = { Text(order.status.replaceFirstChar { it.uppercase() }) })
            }
            Text(readableDate(order.createdAt))
            Text("Pago: ${order.paymentMethod.replaceFirstChar { it.uppercase() }}")
            Text(
                money(order.total),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

internal fun readableDate(value: String): String = value.replace("T", " ").take(16)
