package com.example.proyectomovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomovil.data.model.Producto
import com.example.proyectomovil.ui.viewmodel.CarritoViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoDetalleScreen(
    producto: Producto,
    clienteId: Long?,
    onVolverClick: () -> Unit,
    onAgregarExitoso: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    var cantidad by remember { mutableStateOf(1) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // Imagen grande
            AsyncImage(
                model = producto.imagenUrl ?: "",
                contentDescription = producto.nombreProducto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nombre y marca
                Text(
                    text = producto.nombreProducto,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = producto.marca?.nombreMarca ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                // Precio
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Precio",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "$${String.format("%.0f", producto.precio)}",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider()

                // Descripción
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = producto.descripcion ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyLarge
                )

                Divider()

                // Especificaciones
                Text(
                    text = "Especificaciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                SpecRow("Categoría", producto.categoria?.nombreCategoria ?: "-")
                SpecRow("Tipo", producto.tipoProducto?.nombreTipo ?: "-")
                SpecRow("Género", producto.genero?.nombreGenero ?: "-")
                SpecRow("Volumen", "${producto.volumenML}ml")
                SpecRow("Aroma", producto.aroma ?: "-")
                SpecRow("Familia Olfativa", producto.familiaOlfativa ?: "-")
                SpecRow(
                    "Stock",
                    "${producto.stock} unidades",
                    if (producto.stock > 10) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )

                Divider()

                // Selector de cantidad (solo si es cliente)
                if (clienteId != null) {
                    Text(
                        text = "Cantidad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { if (cantidad > 1) cantidad-- },
                            enabled = cantidad > 1
                        ) {
                            Text("-", style = MaterialTheme.typography.titleLarge)
                        }

                        Text(
                            text = cantidad.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        OutlinedButton(
                            onClick = { if (cantidad < producto.stock) cantidad++ },
                            enabled = cantidad < producto.stock
                        ) {
                            Text("+", style = MaterialTheme.typography.titleLarge)
                        }
                    }

                    // Botón agregar al carrito
                    Button(
                        onClick = {
                            if (producto.stock >= cantidad && clienteId != null) {
                                // ✅ CORREGIDO: Validamos que clienteId no sea null
                                // antes de llamar a agregarProducto
                                carritoViewModel.agregarProducto(
                                    producto.idProducto,
                                    clienteId, // Ahora es seguro porque validamos != null
                                    cantidad
                                )
                                mostrarDialogo = true
                            }
                        },
                        enabled = producto.stock > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (producto.stock > 0) "Agregar al Carrito"
                            else "Sin Stock",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Diálogo de confirmación
        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogo = false },
                title = { Text("¡Producto Agregado!") },
                text = { Text("Se agregó $cantidad ${if (cantidad == 1) "unidad" else "unidades"} al carrito") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarDialogo = false
                        onAgregarExitoso()
                    }) {
                        Text("Ver Carrito")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarDialogo = false
                        onVolverClick()
                    }) {
                        Text("Seguir Comprando")
                    }
                }
            )
        }
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}