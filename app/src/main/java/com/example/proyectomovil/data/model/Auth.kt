package com.example.proyectomovil.data.model

data class LoginRequest(
    val correo: String,
    val contrasena: String
)

data class LoginResponse(
    val mensaje: String,
    val nombreUsuario: String?,
    val autenticado: Boolean,
    val token: String?
)