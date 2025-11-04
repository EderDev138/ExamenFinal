package com.example.proyectomovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomovil.data.model.Pedido
import com.example.proyectomovil.ui.viewmodel.PedidosViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    clienteId: Long,
    onVolverClick: () -> Unit,
    viewModel: PedidosViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(clienteId) {
        viewModel.cargarPedidos(clienteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.pedidos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No tienes pedidos aún",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tus compras aparecerán aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onVolverClick) {
                    Text("Ir a comprar")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.pedidos, key = { it.id }) { pedido ->
                    PedidoCard(pedido = pedido)
                }
            }
        }

        state.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(error)
            }
        }
    }
}

@Composable
private fun PedidoCard(pedido: Pedido) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pedido #${pedido.id}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatearFecha(pedido.fechaPedido),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                EstadoChip(estado = pedido.estado)
            }

            Divider()

            // Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$${String.format("%.0f", pedido.total.toDouble())}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Dirección de envío
            if (expandido) {
                Divider()

                Text(
                    text = "Dirección de Envío",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = pedido.direccionEnvio,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${pedido.comunaEnvio}, ${pedido.regionEnvio}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Detalles del pedido
                Divider()

                Text(
                    text = "Detalles",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                    Text("$${String.format("%.0f", pedido.subtotal.toDouble())}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Descuento:", style = MaterialTheme.typography.bodyMedium)
                    Text("$${String.format("%.0f", pedido.descuento.toDouble())}")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("IVA:", style = MaterialTheme.typography.bodyMedium)
                    Text("$${String.format("%.0f", pedido.iva.toDouble())}")
                }

                pedido.numeroSeguimiento?.let { tracking ->
                    Divider()
                    Text(
                        text = "N° Seguimiento: $tracking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Botón expandir/contraer
            TextButton(
                onClick = { expandido = !expandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expandido) "Ver menos" else "Ver más detalles")
            }
        }
    }
}

@Composable
private fun EstadoChip(estado: String) {
    val (color, texto) = when (estado.uppercase()) {
        "PENDIENTE" -> MaterialTheme.colorScheme.tertiaryContainer to "Pendiente"
        "PROCESANDO" -> MaterialTheme.colorScheme.secondaryContainer to "Procesando"
        "ENVIADO" -> MaterialTheme.colorScheme.primaryContainer to "Enviado"
        "ENTREGADO" -> MaterialTheme.colorScheme.primary to "Entregado"
        "CANCELADO" -> MaterialTheme.colorScheme.errorContainer to "Cancelado"
        else -> MaterialTheme.colorScheme.surfaceVariant to estado
    }

    AssistChip(
        onClick = {},
        label = { Text(texto) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color
        )
    )
}

private fun formatearFecha(fecha: String?): String {
    if (fecha == null) return "Fecha no disponible"

    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(fecha, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        fecha
    }
}