package com.example.proyectomovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomovil.data.model.*
import com.example.proyectomovil.ui.viewmodel.GestionProductosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionProductosScreen(
    onVolverClick: () -> Unit,
    viewModel: GestionProductosViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var mostrarFormulario by remember { mutableStateOf(false) }

    // Estado del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var volumen by remember { mutableStateOf("") }
    var marcaSeleccionada by remember { mutableStateOf<Marca?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var generoSeleccionado by remember { mutableStateOf<Genero?>(null) }
    var tipoSeleccionado by remember { mutableStateOf<TipoProducto?>(null) }
    var aroma by remember { mutableStateOf("") }
    var familiaOlfativa by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // Cuando se selecciona un producto para editar
    LaunchedEffect(state.productoEditando) {
        state.productoEditando?.let { producto ->
            nombre = producto.nombreProducto
            descripcion = producto.descripcion ?: ""
            precio = producto.precio.toString()
            volumen = producto.volumenML.toString()
            marcaSeleccionada = producto.marca
            categoriaSeleccionada = producto.categoria
            generoSeleccionado = producto.genero
            tipoSeleccionado = producto.tipoProducto
            aroma = producto.aroma ?: ""
            familiaOlfativa = producto.familiaOlfativa ?: ""
            imagenUrl = producto.imagenUrl ?: ""
            stock = producto.stock.toString()
            mostrarFormulario = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Productos") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!mostrarFormulario) {
                FloatingActionButton(
                    onClick = {
                        viewModel.limpiarSeleccion()
                        nombre = ""
                        descripcion = ""
                        precio = ""
                        volumen = ""
                        marcaSeleccionada = null
                        categoriaSeleccionada = null
                        generoSeleccionado = null
                        tipoSeleccionado = null
                        aroma = ""
                        familiaOlfativa = ""
                        imagenUrl = ""
                        stock = ""
                        mostrarFormulario = true
                    }
                ) {
                    Icon(Icons.Default.Add, "Agregar Producto")
                }
            }
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
        } else if (mostrarFormulario) {
            FormularioProducto(
                padding = padding,
                nombre = nombre,
                onNombreChange = { nombre = it },
                descripcion = descripcion,
                onDescripcionChange = { descripcion = it },
                precio = precio,
                onPrecioChange = { precio = it },
                volumen = volumen,
                onVolumenChange = { volumen = it },
                marcas = state.marcas,
                marcaSeleccionada = marcaSeleccionada,
                onMarcaChange = { marcaSeleccionada = it },
                categorias = state.categorias,
                categoriaSeleccionada = categoriaSeleccionada,
                onCategoriaChange = { categoriaSeleccionada = it },
                generos = state.generos,
                generoSeleccionado = generoSeleccionado,
                onGeneroChange = { generoSeleccionado = it },
                tiposProducto = state.tiposProducto,
                tipoSeleccionado = tipoSeleccionado,
                onTipoChange = { tipoSeleccionado = it },
                aroma = aroma,
                onAromaChange = { aroma = it },
                familiaOlfativa = familiaOlfativa,
                onFamiliaOlfativaChange = { familiaOlfativa = it },
                imagenUrl = imagenUrl,
                onImagenUrlChange = { imagenUrl = it },
                stock = stock,
                onStockChange = { stock = it },
                onGuardar = {
                    val request = ProductoRequest(
                        nombreProducto = nombre,
                        descripcion = descripcion.ifBlank { null },
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        volumenML = volumen.toIntOrNull() ?: 0,
                        marca = MarcaRef(marcaSeleccionada?.idMarca ?: 0),
                        categoria = CategoriaRef(categoriaSeleccionada?.idCategoria ?: 0),
                        tipoProducto = TipoProductoRef(tipoSeleccionado?.idTipoProducto ?: 0),
                        genero = GeneroRef(generoSeleccionado?.idGenero ?: 0),
                        aroma = aroma.ifBlank { null },
                        familiaOlfativa = familiaOlfativa.ifBlank { null },
                        imagenUrl = imagenUrl.ifBlank { null },
                        stock = stock.toIntOrNull() ?: 0,
                        activo = true
                    )

                    if (state.productoEditando != null) {
                        viewModel.actualizarProducto(state.productoEditando!!.idProducto, request)
                    } else {
                        viewModel.crearProducto(request)
                    }
                    mostrarFormulario = false
                },
                onCancelar = {
                    mostrarFormulario = false
                    viewModel.limpiarSeleccion()
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.productos, key = { it.idProducto }) { producto ->
                    ProductoItemCard(
                        producto = producto,
                        onEditarClick = {
                            viewModel.seleccionarProducto(producto)
                        },
                        onEliminarClick = {
                            viewModel.eliminarProducto(producto.idProducto)
                        }
                    )
                }
            }
        }

        // Mensajes
        state.exitoMessage?.let { mensaje ->
            LaunchedEffect(mensaje) {
                kotlinx.coroutines.delay(2000)
                viewModel.limpiarMensajes()
            }
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(mensaje)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioProducto(
    padding: PaddingValues,
    nombre: String,
    onNombreChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    precio: String,
    onPrecioChange: (String) -> Unit,
    volumen: String,
    onVolumenChange: (String) -> Unit,
    marcas: List<Marca>,
    marcaSeleccionada: Marca?,
    onMarcaChange: (Marca?) -> Unit,
    categorias: List<Categoria>,
    categoriaSeleccionada: Categoria?,
    onCategoriaChange: (Categoria?) -> Unit,
    generos: List<Genero>,
    generoSeleccionado: Genero?,
    onGeneroChange: (Genero?) -> Unit,
    tiposProducto: List<TipoProducto>,
    tipoSeleccionado: TipoProducto?,
    onTipoChange: (TipoProducto?) -> Unit,
    aroma: String,
    onAromaChange: (String) -> Unit,
    familiaOlfativa: String,
    onFamiliaOlfativaChange: (String) -> Unit,
    imagenUrl: String,
    onImagenUrlChange: (String) -> Unit,
    stock: String,
    onStockChange: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Información del Producto",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = { Text("Nombre *") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = precio,
                onValueChange = onPrecioChange,
                label = { Text("Precio *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = volumen,
                onValueChange = onVolumenChange,
                label = { Text("Volumen (ml) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        OutlinedTextField(
            value = stock,
            onValueChange = onStockChange,
            label = { Text("Stock *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdowns
        var expandedMarca by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedMarca,
            onExpandedChange = { expandedMarca = !expandedMarca }
        ) {
            OutlinedTextField(
                value = marcaSeleccionada?.nombreMarca ?: "Seleccionar",
                onValueChange = {},
                readOnly = true,
                label = { Text("Marca *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMarca) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedMarca,
                onDismissRequest = { expandedMarca = false }
            ) {
                marcas.forEach { marca ->
                    DropdownMenuItem(
                        text = { Text(marca.nombreMarca) },
                        onClick = {
                            onMarcaChange(marca)
                            expandedMarca = false
                        }
                    )
                }
            }
        }

        var expandedCategoria by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedCategoria,
            onExpandedChange = { expandedCategoria = !expandedCategoria }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada?.nombreCategoria ?: "Seleccionar",
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedCategoria,
                onDismissRequest = { expandedCategoria = false }
            ) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.nombreCategoria) },
                        onClick = {
                            onCategoriaChange(categoria)
                            expandedCategoria = false
                        }
                    )
                }
            }
        }

        var expandedGenero by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedGenero,
            onExpandedChange = { expandedGenero = !expandedGenero }
        ) {
            OutlinedTextField(
                value = generoSeleccionado?.nombreGenero ?: "Seleccionar",
                onValueChange = {},
                readOnly = true,
                label = { Text("Género *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGenero) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedGenero,
                onDismissRequest = { expandedGenero = false }
            ) {
                generos.forEach { genero ->
                    DropdownMenuItem(
                        text = { Text(genero.nombreGenero) },
                        onClick = {
                            onGeneroChange(genero)
                            expandedGenero = false
                        }
                    )
                }
            }
        }

        var expandedTipo by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expandedTipo,
            onExpandedChange = { expandedTipo = !expandedTipo }
        ) {
            OutlinedTextField(
                value = tipoSeleccionado?.nombreTipo ?: "Seleccionar",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedTipo,
                onDismissRequest = { expandedTipo = false }
            ) {
                tiposProducto.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.nombreTipo) },
                        onClick = {
                            onTipoChange(tipo)
                            expandedTipo = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = aroma,
            onValueChange = onAromaChange,
            label = { Text("Aroma") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = familiaOlfativa,
            onValueChange = onFamiliaOlfativaChange,
            label = { Text("Familia Olfativa") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = imagenUrl,
            onValueChange = onImagenUrlChange,
            label = { Text("URL de Imagen") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = onGuardar,
                modifier = Modifier.weight(1f),
                enabled = nombre.isNotBlank() && precio.toDoubleOrNull() != null &&
                        volumen.toIntOrNull() != null && stock.toIntOrNull() != null &&
                        marcaSeleccionada != null && categoriaSeleccionada != null &&
                        generoSeleccionado != null && tipoSeleccionado != null
            ) {
                Text("Guardar")
            }
        }
    }
}

@Composable
private fun ProductoItemCard(
    producto: Producto,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = producto.nombreProducto,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${producto.marca?.nombreMarca} | Stock: ${producto.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$${String.format("%.0f", producto.precio)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = onEditarClick) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(onClick = onEliminarClick) {
                    Icon(
                        Icons.Default.Delete,
                        "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}