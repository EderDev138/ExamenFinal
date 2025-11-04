package com.example.proyectomovil.utils

fun validarRut(rut: String): Boolean {
    val regex = Regex("^\\d{7,8}-[\\dkK]$")
    if (!regex.matches(rut)) return false

    val partes = rut.split("-")
    val numero = partes[0].toIntOrNull() ?: return false
    val dv = partes[1].uppercase()

    return calcularDV(numero) == dv
}

fun calcularDV(rut: Int): String {
    var suma = 0
    var multiplicador = 2
    var rutTemp = rut

    while (rutTemp > 0) {
        suma += (rutTemp % 10) * multiplicador
        rutTemp /= 10
        multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
    }

    val resto = suma % 11
    val dv = 11 - resto

    return when (dv) {
        11 -> "0"
        10 -> "K"
        else -> dv.toString()
    }
}

fun validarEmail(email: String): Boolean {
    val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return regex.matches(email)
}

fun formatearPrecio(precio: Double): String {
    return "$${String.format("%,.0f", precio)}"
}