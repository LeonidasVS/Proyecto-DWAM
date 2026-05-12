package com.example.shopnova.Model

data class CarritoItem(
    val producto: Producto,
    var cantidad: Int = 1
) {
    fun subtotal(): Double = producto.price * cantidad
}