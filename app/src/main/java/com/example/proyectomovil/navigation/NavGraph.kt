package com.example.proyectomovil.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyectomovil.ui.screens.*
import com.example.proyectomovil.ui.viewmodel.CarritoViewModel

/**
 * Sealed class que define todas las rutas de navegación de la aplicación.
 * Cada objeto representa una pantalla con su ruta y parámetros.
 *
 * Las rutas utilizan el formato de Navigation Compose donde:
 * - {parametro} indica un parámetro obligatorio en la URL
 * - {parametro?} indica un parámetro opcional
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Registro : Screen("registro")

    object Home : Screen("home/{userId}/{rol}") {
        fun createRoute(userId: Long, rol: String) = "home/$userId/$rol"
        fun createGuestRoute() = "home/3/INVITADO"
    }

    object Carrito : Screen("carrito/{clienteId}") {
        fun createRoute(clienteId: Long) = "carrito/$clienteId"
    }

    object Checkout : Screen("checkout/{clienteId}") {
        fun createRoute(clienteId: Long) = "checkout/$clienteId"
    }

    // ==================== NUEVAS RUTAS AGREGADAS ====================

    /**
     * Pantalla de detalle de producto.
     * Muestra información completa del producto y permite agregarlo al carrito.
     *
     * @param productoId: ID del producto a mostrar
     * @param clienteId: ID del cliente (opcional, si no está logueado será null)
     */
    object ProductoDetalle : Screen("productoDetalle/{productoId}/{clienteId}") {
        // Crea ruta cuando hay cliente logueado
        fun createRoute(productoId: Long, clienteId: Long?) =
            "productoDetalle/$productoId/${clienteId ?: 0}"
    }

    /**
     * Pantalla de perfil del usuario.
     * Muestra información personal y opciones de cuenta.
     *
     * @param userId: ID del usuario
     */
    object Perfil : Screen("perfil/{userId}") {
        fun createRoute(userId: Long) = "perfil/$userId"
    }

    /**
     * Pantalla de mis pedidos.
     * Lista todos los pedidos históricos del cliente.
     *
     * @param clienteId: ID del cliente
     */
    object MisPedidos : Screen("misPedidos/{clienteId}") {
        fun createRoute(clienteId: Long) = "misPedidos/$clienteId"
    }

    /**
     * Pantalla de gestión de productos (CRUD).
     * Solo accesible para roles ENCARGADO y ADMIN.
     */
    object GestionProductos : Screen("gestionProductos")

    /**
     * Pantalla de gestión de usuarios.
     * Permite aprobar/desactivar usuarios ENCARGADO.
     * Solo accesible para rol ADMIN.
     */
    object GestionUsuarios : Screen("gestionUsuarios")

    /**
     * Pantalla de gestión de datos maestros.
     * Permite gestionar Marcas, Categorías, Géneros y Tipos de Producto.
     * Solo accesible para roles ENCARGADO y ADMIN.
     */
    object GestionDatosMaestros : Screen("gestionDatosMaestros")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { userId, rol ->
                    navController.navigate(Screen.Home.createRoute(userId, rol)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegistroClick = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }

        // Registro
        composable(Screen.Registro.route) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.popBackStack()
                },
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // ==================== PANTALLA HOME (CATÁLOGO) ====================
        /**
         * Pantalla principal con el catálogo de productos.
         * Recibe userId y rol para personalizar la experiencia del usuario.
         */
        composable(
            route = Screen.Home.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            val rol = backStackEntry.arguments?.getString("rol") ?: "CLIENTE"

            HomeScreen(
                rol = rol,
                // Al hacer clic en un producto, navegar a la pantalla de detalle
                onProductoClick = { productoId ->
                    navController.navigate(
                        Screen.ProductoDetalle.createRoute(productoId, userId)
                    )
                },
                // Ir al carrito de compras
                onCarritoClick = {
                    navController.navigate(Screen.Carrito.createRoute(userId))
                },
                // Ver perfil del usuario
                onPerfilClick = {
                    navController.navigate(Screen.Perfil.createRoute(userId))
                },
                // ENCARGADO/ADMIN: Gestionar productos
                onGestionProductosClick = {
                    navController.navigate(Screen.GestionProductos.route)
                },
                // ADMIN: Gestionar usuarios
                onGestionUsuariosClick = {
                    navController.navigate(Screen.GestionUsuarios.route)
                },
                // ENCARGADO/ADMIN: Gestionar datos maestros
                onGestionDatosMaestrosClick = {
                    navController.navigate(Screen.GestionDatosMaestros.route)
                },
                // CLIENTE: Ver historial de pedidos
                onMisPedidosClick = {
                    navController.navigate(Screen.MisPedidos.createRoute(userId))
                }
            )
        }

        // Carrito
        composable(
            route = Screen.Carrito.route,
            arguments = listOf(
                navArgument("clienteId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getLong("clienteId") ?: 0L

            CarritoScreen(
                clienteId = clienteId,
                onVolverClick = {
                    navController.popBackStack()
                },
                onCheckoutClick = {
                    navController.navigate(Screen.Checkout.createRoute(clienteId))
                }
            )
        }

        // ==================== PANTALLA CHECKOUT ====================
        /**
         * Pantalla de finalización de compra.
         * Permite al cliente confirmar su pedido y proporcionar datos de envío.
         */
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("clienteId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getLong("clienteId") ?: 0L
            val carritoViewModel: CarritoViewModel = viewModel()
            val carritoState by carritoViewModel.state.collectAsState()

            CheckoutScreen(
                clienteId = clienteId,
                carrito = carritoState.items,
                subtotal = carritoState.subtotal,
                iva = carritoState.iva,
                total = carritoState.total,
                onVolverClick = {
                    navController.popBackStack()
                },
                onCheckoutExitoso = {
                    // Al completar el pedido, volver al home y limpiar el stack
                    navController.navigate(Screen.Home.createRoute(clienteId, "CLIENTE")) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ==================== PANTALLA DETALLE DE PRODUCTO ====================
        /**
         * Muestra información detallada de un producto específico.
         * Permite agregar el producto al carrito si el usuario está logueado.
         *
         * Parámetros:
         * - productoId: ID del producto a mostrar
         * - clienteId: ID del cliente (0 si no está logueado)
         */
        composable(
            route = Screen.ProductoDetalle.route,
            arguments = listOf(
                navArgument("productoId") { type = NavType.LongType },
                navArgument("clienteId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getLong("productoId") ?: 0L
            val clienteId = backStackEntry.arguments?.getLong("clienteId") ?: 0L

            // Obtener el producto desde el ViewModel compartido
            val productosViewModel: com.example.proyectomovil.ui.viewmodel.ProductosViewModel = viewModel()
            val state by productosViewModel.state.collectAsState()

            // Buscar el producto específico en la lista cargada
            val producto = state.productos.find { it.idProducto == productoId }

            // Solo mostrar la pantalla si se encontró el producto
            if (producto != null) {
                ProductoDetalleScreen(
                    producto = producto,
                    clienteId = if (clienteId > 0) clienteId else null,
                    onVolverClick = {
                        navController.popBackStack()
                    },
                    onAgregarExitoso = {
                        // ✅ CORREGIDO: Validamos que clienteId sea válido antes de navegar
                        // Si clienteId es 0, significa que el usuario no está logueado
                        // y no debería poder agregar al carrito
                        if (clienteId > 0) {
                            navController.navigate(Screen.Carrito.createRoute(clienteId))
                        }
                    }
                )
            }
        }

        // ==================== PANTALLA PERFIL ====================
        /**
         * Muestra el perfil del usuario con su información personal.
         * Permite editar datos, cambiar contraseña y cerrar sesión.
         *
         * Parámetros:
         * - userId: ID del usuario
         *
         * Funcionamiento:
         * 1. Crea una instancia del PerfilViewModel
         * 2. Carga el usuario desde el backend usando el userId
         * 3. Muestra la pantalla de perfil cuando los datos están listos
         * 4. Muestra indicador de carga mientras se obtienen los datos
         */
        composable(
            route = Screen.Perfil.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L

            // Crear ViewModel específico para perfil
            val perfilViewModel: com.example.proyectomovil.ui.viewmodel.PerfilViewModel = viewModel()
            val perfilState by perfilViewModel.state.collectAsState()

            // Cargar el usuario al entrar a la pantalla
            // LaunchedEffect se ejecuta solo una vez cuando cambia userId
            LaunchedEffect(userId) {
                perfilViewModel.cargarUsuario(userId)
            }

            // Mostrar diferentes estados de la UI
            when {
                // Caso 1: Está cargando - Mostrar indicador de progreso
                perfilState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Caso 2: Hubo un error - Mostrar mensaje de error
                perfilState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = perfilState.errorMessage ?: "Error desconocido",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { perfilViewModel.cargarUsuario(userId) }
                            ) {
                                Text("Reintentar")
                            }
                            TextButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Text("Volver")
                            }
                        }
                    }
                }

                // Caso 3: Usuario cargado exitosamente - Mostrar pantalla de perfil
                perfilState.usuario != null -> {
                    PerfilScreen(
                        usuario = perfilState.usuario!!,
                        onVolverClick = {
                            navController.popBackStack()
                        },
                        onCerrarSesion = {
                            // Cerrar sesión y volver al login, limpiando todo el stack de navegación
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // ==================== PANTALLA MIS PEDIDOS ====================
        /**
         * Muestra el historial de pedidos del cliente.
         * Lista todos los pedidos con su estado y detalles.
         *
         * Parámetros:
         * - clienteId: ID del cliente
         */
        composable(
            route = Screen.MisPedidos.route,
            arguments = listOf(
                navArgument("clienteId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val clienteId = backStackEntry.arguments?.getLong("clienteId") ?: 0L

            MisPedidosScreen(
                clienteId = clienteId,
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // ==================== PANTALLA GESTIÓN DE PRODUCTOS ====================
        /**
         * Pantalla administrativa para gestionar productos (CRUD).
         * Solo accesible para usuarios con rol ENCARGADO o ADMIN.
         *
         * Funcionalidades:
         * - Crear nuevos productos
         * - Editar productos existentes
         * - Eliminar productos
         * - Ver lista completa de productos
         */
        composable(route = Screen.GestionProductos.route) {
            GestionProductosScreen(
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // ==================== PANTALLA GESTIÓN DE USUARIOS ====================
        /**
         * Pantalla administrativa para gestionar usuarios ENCARGADO.
         * Solo accesible para usuarios con rol ADMIN.
         *
         * Funcionalidades:
         * - Aprobar usuarios ENCARGADO pendientes
         * - Desactivar usuarios ENCARGADO activos
         * - Ver lista de usuarios por estado (pendientes/activos)
         */
        composable(route = Screen.GestionUsuarios.route) {
            GestionUsuariosScreen(
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }

        // ==================== PANTALLA GESTIÓN DE DATOS MAESTROS ====================
        /**
         * Pantalla administrativa para gestionar datos maestros.
         * Solo accesible para usuarios con rol ENCARGADO o ADMIN.
         *
         * Funcionalidades:
         * - Crear, editar y eliminar Marcas
         * - Crear, editar y eliminar Categorías
         * - Crear, editar y eliminar Géneros
         * - Crear, editar y eliminar Tipos de Producto
         */
        composable(route = Screen.GestionDatosMaestros.route) {
            GestionDatosMaestrosScreen(
                onVolverClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}