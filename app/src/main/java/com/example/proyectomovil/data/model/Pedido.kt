package com.example.proyectomovil.data.model

import java.math.BigDecimal

data class Pedido(
    val id: Long = 0,
    val cliente: ClienteRef? = null,
    val fechaPedido: String? = null,
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val descuento: BigDecimal = BigDecimal.ZERO,
    val iva: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val estado: String = "PENDIENTE",
    val direccionEnvio: String = "",
    val comunaEnvio: String = "",
    val regionEnvio: String = "",
    val numeroSeguimiento: String? = null,
    val promocionAplicada: Any? = null,
    val fechaActualizacion: String? = null
)

data class PedidoRequest(
    val cliente: ClienteRef,
    val subtotal: Double,
    val descuento: Double,
    val iva: Double,
    val total: Double,
    val estado: String,
    val direccionEnvio: String,
    val comunaEnvio: String,
    val regionEnvio: String
)

data class DetallePedido(
    val id: Long = 0,
    val pedido: PedidoRef? = null,
    val producto: Producto? = null,
    val cantidad: Int = 0,
    val precioUnitario: BigDecimal = BigDecimal.ZERO,
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val descuentoAplicado: BigDecimal = BigDecimal.ZERO
)

data class DetallePedidoRequest(
    val pedido: PedidoRef,
    val producto: ProductoRef,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
    val descuentoAplicado: Double
)

data class PedidoRef(val id: Long)