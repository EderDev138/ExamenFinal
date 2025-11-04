package com.example.proyectomovil.data.model

data class Producto(
    val idProducto: Long = 0,
    val nombreProducto: String = "",
    val descripcion: String? = null,
    val precio: Double = 0.0,
    val volumenML: Int = 0,
    val marca: Marca? = null,
    val categoria: Categoria? = null,
    val tipoProducto: TipoProducto? = null,
    val genero: Genero? = null,
    val aroma: String? = null,
    val familiaOlfativa: String? = null,
    val imagenUrl: String? = null,
    val stock: Int = 0,
    val activo: Boolean = true,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)

data class ProductoRequest(
    val nombreProducto: String,
    val descripcion: String?,
    val precio: Double,
    val volumenML: Int,
    val marca: MarcaRef,
    val categoria: CategoriaRef,
    val tipoProducto: TipoProductoRef,
    val genero: GeneroRef,
    val aroma: String?,
    val familiaOlfativa: String?,
    val imagenUrl: String?,
    val stock: Int,
    val activo: Boolean = true
)

// Referencias para requests
data class MarcaRef(val idMarca: Long)
data class CategoriaRef(val idCategoria: Long)
data class GeneroRef(val idGenero: Long)
data class TipoProductoRef(val idTipoProducto: Long)