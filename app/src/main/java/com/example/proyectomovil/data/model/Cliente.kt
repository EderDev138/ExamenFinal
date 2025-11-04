package com.example.proyectomovil.data.model

data class Cliente(
    val id: Long = 0,
    val usuario: Usuario? = null,
    val rut: String = "",
    val primerNombre: String = "",
    val segundoNombre: String? = null,
    val primerApellido: String = "",
    val segundoApellido: String? = null,
    val fechaNacimiento: String = "",
    val direccion: String = "",
    val comuna: String = "",
    val region: String = "",
    val telefono: String = "",
    val fechaCreacion: String? = null
)

data class ClienteRequest(
    val usuario: Usuario,
    val rut: String,
    val primerNombre: String,
    val segundoNombre: String?,
    val primerApellido: String,
    val segundoApellido: String?,
    val fechaNacimiento: String,
    val direccion: String,
    val comuna: String,
    val region: String,
    val telefono: String
)