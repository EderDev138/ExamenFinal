package com.example.proyectomovil.data.model

data class Carrito(
    val id: Long = 0,
    val cliente: ClienteRef? = null,
    val producto: Producto? = null,
    val cantidad: Int = 0,
    val fechaAgregado: String? = null,
    val activo: Boolean = true
)

data class CarritoRequest(
    val cliente: ClienteRef,
    val producto: ProductoRef,
    val cantidad: Int
)

data class ClienteRef(val id: Long)
data class ProductoRef(val idProducto: Long)