package com.example.proyectomovil.data.model


data class Marca(
    val idMarca: Long = 0,
    val nombreMarca: String = "",
    val descripcion: String? = null,
    val paisOrigen: String? = null
)

data class Categoria(
    val idCategoria: Long = 0,
    val nombreCategoria: String = "",
    val descripcion: String? = null
)

data class Genero(
    val idGenero: Long = 0,
    val nombreGenero: String = ""
)

data class TipoProducto(
    val idTipoProducto: Long = 0,
    val nombreTipo: String = "",
    val descripcion: String? = null
)

// ==================== MODELOS REQUEST PARA CREAR/ACTUALIZAR ====================

/**
 * Modelo para crear o actualizar una Marca.
 * Se usa en las peticiones POST y PUT al backend.
 *
 * Campos requeridos según el backend:
 * - nombreMarca: No puede ser null, único, máximo 100 caracteres
 *
 * Campos opcionales:
 * - descripcion: Máximo 255 caracteres
 * - paisOrigen: Máximo 255 caracteres
 */
data class MarcaRequest(
    val nombreMarca: String,
    val descripcion: String? = null,
    val paisOrigen: String? = null
)

/**
 * Modelo para crear o actualizar una Categoría.
 * Se usa en las peticiones POST y PUT al backend.
 *
 * Campos requeridos según el backend:
 * - nombreCategoria: No puede ser null, único, máximo 100 caracteres
 *
 * Campos opcionales:
 * - descripcion: Máximo 255 caracteres
 */
data class CategoriaRequest(
    val nombreCategoria: String,
    val descripcion: String? = null
)

/**
 * Modelo para crear o actualizar un Género.
 * Se usa en las peticiones POST y PUT al backend.
 *
 * Campos requeridos según el backend:
 * - nombreGenero: No puede ser null, único, máximo 50 caracteres
 */
data class GeneroRequest(
    val nombreGenero: String
)

/**
 * Modelo para crear o actualizar un Tipo de Producto.
 * Se usa en las peticiones POST y PUT al backend.
 *
 * Campos requeridos según el backend:
 * - nombreTipo: No puede ser null, único, máximo 100 caracteres
 *
 * Campos opcionales:
 * - descripcion: Máximo 255 caracteres
 */
data class TipoProductoRequest(
    val nombreTipo: String,
    val descripcion: String? = null
)