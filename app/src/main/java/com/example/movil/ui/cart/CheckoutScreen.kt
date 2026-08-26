package com.example.movil.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.movil.data.orders.PaymentMethod
import com.example.movil.ui.orders.OrdersViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderConfirmed: (Int) -> Unit,
    onUnauthorized: () -> Unit,
    cartViewModel: CartViewModel = viewModel(),
    ordersViewModel: OrdersViewModel = viewModel()
) {
    val cartState by cartViewModel.uiState.collectAsState()
    val orderState by ordersViewModel.uiState.collectAsState()
    var selected by remember { mutableStateOf(PaymentMethod.CARD) }
    var showConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(cartState.unauthorized, orderState.unauthorized) {
        if (cartState.unauthorized || orderState.unauthorized) onUnauthorized()
    }
    LaunchedEffect(orderState.stockConflict) {
        if (orderState.stockConflict) cartViewModel.loadCart()
    }
    LaunchedEffect(orderState.confirmedOrder?.id) {
        orderState.confirmedOrder?.let {
            cartViewModel.loadCart()
            onOrderConfirmed(it.id)
            ordersViewModel.consumeConfirmation()
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Confirmar pedido") }, navigationIcon = {
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
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Column(Modifier
                    .fillMaxWidth()
                    .padding(18.dp)) {
                    Text("Resumen de compra", style = MaterialTheme.typography.titleLarge)
                    Text("${cartState.cart.count} ejemplares")
                    Text(
                        money(cartState.cart.total),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("Método de pago", style = MaterialTheme.typography.titleMedium)
            PaymentMethod.entries.forEach { method ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected == method, onClick = { selected = method })
                    Text(method.label)
                }
            }
            Text(
                "La aplicación solo registra el método; no solicita datos bancarios.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.weight(1f))
            orderState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = { showConfirmation = true },
                enabled = cartState.cart.items.isNotEmpty() && !orderState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (orderState.isLoading) CircularProgressIndicator(
                    Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                else Text("Confirmar pedido")
            }
        }
    }

    if (showConfirmation) AlertDialog(
        onDismissRequest = { showConfirmation = false },
        title = { Text("Confirmar compra") },
        text = { Text("¿Confirmas el pedido por ${money(cartState.cart.total)} mediante ${selected.label}?") },
        confirmButton = {
            TextButton(onClick = { showConfirmation = false; ordersViewModel.checkout(selected) }) {
                Text("Confirmar")
            }
        },
        dismissButton = { TextButton(onClick = { showConfirmation = false }) { Text("Cancelar") } }
    )
}
