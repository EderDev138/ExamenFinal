package com.example.proyectomovil.data.model

data class Usuario(
    val idUsuario: Long = 0,
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val activo: Boolean = true,
    val roles: List<Rol> = emptyList()
)

data class Rol(
    val idRol: Long = 0,
    val nombreRol: String = "",
    val descripcionRol: String? = null
)