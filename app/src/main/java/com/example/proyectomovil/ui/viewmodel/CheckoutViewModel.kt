package com.example.proyectomovil.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectomovil.data.model.*
import com.example.proyectomovil.data.repository.RinconRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutUiState(
    val direccion: String = "",
    val comuna: String = "",
    val region: String = "Metropolitana",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pedidoCreado: Pedido? = null,
    val checkoutExitoso: Boolean = false
)

class CheckoutViewModel : ViewModel() {

    private val repository = RinconRepository()

    private val _state = MutableStateFlow(CheckoutUiState())
    val state: StateFlow<CheckoutUiState> = _state.asStateFlow()

    fun onDireccionChange(value: String) {
        _state.update { it.copy(direccion = value, errorMessage = null) }
    }

    fun onComunaChange(value: String) {
        _state.update { it.copy(comuna = value, errorMessage = null) }
    }

    fun onRegionChange(value: String) {
        _state.update { it.copy(region = value, errorMessage = null) }
    }

    fun realizarCheckout(
        clienteId: Long,
        carrito: List<Carrito>,
        subtotal: Double,
        iva: Double,
        total: Double
    ) {
        val s = _state.value

        if (s.direccion.isBlank() || s.comuna.isBlank() || s.region.isBlank()) {
            _state.update { it.copy(errorMessage = "Completa los datos de envío") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // PASO 1: Validar stock de todos los productos
                for (item in carrito) {
                    val productoResult = repository.getProducto(item.producto?.idProducto ?: 0)
                    if (productoResult.isFailure) {
                        throw Exception("Error al validar stock")
                    }

                    val producto = productoResult.getOrNull()
                    if (producto == null || producto.stock < item.cantidad) {
                        throw Exception("Stock insuficiente para ${producto?.nombreProducto}")
                    }
                }

                // PASO 2: Crear pedido
                val pedidoRequest = PedidoRequest(
                    cliente = ClienteRef(clienteId),
                    subtotal = subtotal,
                    descuento = 0.0,
                    iva = iva,
                    total = total,
                    estado = "PENDIENTE",
                    direccionEnvio = s.direccion,
                    comunaEnvio = s.comuna,
                    regionEnvio = s.region
                )

                val pedidoResult = repository.crearPedido(pedidoRequest)
                if (pedidoResult.isFailure) {
                    throw Exception("Error al crear pedido")
                }

                val pedido = pedidoResult.getOrNull()!!

                // PASO 3: Crear detalles del pedido
                for (item in carrito) {
                    val detalleRequest = DetallePedidoRequest(
                        pedido = PedidoRef(pedido.id),
                        producto = ProductoRef(item.producto?.idProducto ?: 0),
                        cantidad = item.cantidad,
                        precioUnitario = item.producto?.precio ?: 0.0,
                        subtotal = (item.producto?.precio ?: 0.0) * item.cantidad,
                        descuentoAplicado = 0.0
                    )

                    repository.crearDetallePedido(detalleRequest)
                }

                // PASO 4: Descontar stock
                for (item in carrito) {
                    val productoActual = repository.getProducto(item.producto?.idProducto ?: 0)
                        .getOrNull()!!

                    val productoActualizado = ProductoRequest(
                        nombreProducto = productoActual.nombreProducto,
                        descripcion = productoActual.descripcion,
                        precio = productoActual.precio,
                        volumenML = productoActual.volumenML,
                        marca = MarcaRef(productoActual.marca?.idMarca ?: 0),
                        categoria = CategoriaRef(productoActual.categoria?.idCategoria ?: 0),
                        tipoProducto = TipoProductoRef(productoActual.tipoProducto?.idTipoProducto ?: 0),
                        genero = GeneroRef(productoActual.genero?.idGenero ?: 0),
                        aroma = productoActual.aroma,
                        familiaOlfativa = productoActual.familiaOlfativa,
                        imagenUrl = productoActual.imagenUrl,
                        stock = productoActual.stock - item.cantidad,
                        activo = productoActual.activo
                    )

                    repository.actualizarProducto(productoActual.idProducto, productoActualizado)
                }

                // PASO 5: Vaciar carrito
                repository.vaciarCarrito(clienteId)

                // ÉXITO
                _state.update {
                    it.copy(
                        isLoading = false,
                        pedidoCreado = pedido,
                        checkoutExitoso = true
                    )
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error en checkout: ${e.message}"
                    )
                }
            }
        }
    }

    fun limpiarError() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun reiniciar() {
        _state.update { CheckoutUiState() }
    }
}