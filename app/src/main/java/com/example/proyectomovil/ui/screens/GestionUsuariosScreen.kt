package com.example.proyectomovil.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectomovil.data.model.Usuario
import com.example.proyectomovil.ui.viewmodel.GestionUsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionUsuariosScreen(
    onVolverClick: () -> Unit,
    viewModel: GestionUsuariosViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var tabSeleccionado by remember { mutableStateOf(0) }
    val tabs = listOf("Pendientes", "Activos")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
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
        ) {
            // Tabs
            TabRow(selectedTabIndex = tabSeleccionado) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick = { tabSeleccionado = index },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(title)
                                Badge {
                                    Text(
                                        when (index) {
                                            0 -> state.usuariosPendientes.size.toString()
                                            else -> state.usuariosActivos.size.toString()
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (tabSeleccionado) {
                    0 -> UsuariosPendientesList(
                        usuarios = state.usuariosPendientes,
                        onAprobar = { viewModel.aprobarUsuario(it) },
                        onRechazar = { viewModel.desactivarUsuario(it) }
                    )
                    1 -> UsuariosActivosList(
                        usuarios = state.usuariosActivos,
                        onDesactivar = { viewModel.desactivarUsuario(it) }
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

@Composable
private fun UsuariosPendientesList(
    usuarios: List<Usuario>,
    onAprobar: (Usuario) -> Unit,
    onRechazar: (Usuario) -> Unit
) {
    if (usuarios.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "No hay usuarios pendientes",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(usuarios, key = { it.idUsuario }) { usuario ->
                UsuarioPendienteCard(
                    usuario = usuario,
                    onAprobar = { onAprobar(usuario) },
                    onRechazar = { onRechazar(usuario) }
                )
            }
        }
    }
}

@Composable
private fun UsuarioPendienteCard(
    usuario: Usuario,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Badge(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text("Pendiente")
                }
            }

            Text(
                text = "${usuario.nombre} ${usuario.apellido}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = usuario.correo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                usuario.roles.forEach { rol ->
                    AssistChip(
                        onClick = {},
                        label = { Text(rol.nombreRol) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { mostrarDialogo = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rechazar")
                }

                Button(
                    onClick = onAprobar,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aprobar")
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Rechazar Usuario") },
            text = { Text("¿Estás seguro de rechazar a este usuario? Esta acción desactivará su cuenta.") },
            confirmButton = {
                Button(
                    onClick = {
                        onRechazar()
                        mostrarDialogo = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Rechazar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun UsuariosActivosList(
    usuarios: List<Usuario>,
    onDesactivar: (Usuario) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(usuarios, key = { it.idUsuario }) { usuario ->
            UsuarioActivoCard(
                usuario = usuario,
                onDesactivar = { onDesactivar(usuario) }
            )
        }
    }
}

@Composable
private fun UsuarioActivoCard(
    usuario: Usuario,
    onDesactivar: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${usuario.nombre} ${usuario.apellido}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = usuario.correo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    usuario.roles.forEach { rol ->
                        AssistChip(
                            onClick = {},
                            label = { Text(rol.nombreRol, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }

            IconButton(onClick = { mostrarDialogo = true }) {
                Icon(
                    Icons.Default.Block,
                    contentDescription = "Desactivar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Desactivar Usuario") },
            text = { Text("¿Deseas desactivar este usuario? No podrá acceder al sistema.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDesactivar()
                        mostrarDialogo = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Desactivar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}