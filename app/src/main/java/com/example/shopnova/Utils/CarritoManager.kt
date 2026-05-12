package com.example.shopnova.Utils

import com.example.shopnova.Model.CarritoItem
import com.example.shopnova.Model.Producto

object CarritoManager {
    // Lista de items en el carrito
    private val items = mutableListOf< CarritoItem>()

    // Obtener todos los items
    fun getItems(): List<CarritoItem> = items.toList()

    // Agregar producto al carrito
    fun agregarProducto(producto: Producto) {
        val itemExistente = items.find { it.producto.id == producto.id }

        if (itemExistente != null) {

            // Si ya existe solo aumenta la cantidad
            // Verifica que no supere el stock disponible
            if (itemExistente.cantidad < producto.stock) {
                itemExistente.cantidad++
            }
        } else {
            // Si no existe lo agrega como nuevo item
            if (producto.stock > 0) {
                items.add(CarritoItem(producto = producto, cantidad = 1))
            }
        }
    }

    // Eliminar producto del carrito
    fun eliminarProducto(productoId: String) {
        items.removeAll { it.producto.id == productoId }
    }

    // Aumentar cantidad
    fun aumentarCantidad(productoId: String) {
        val item = items.find { it.producto.id == productoId }
        item?.let {
            if (it.cantidad < it.producto.stock) {
                it.cantidad++
            }
        }
    }

    // Disminuir cantidad
    fun disminuirCantidad(productoId: String) {
        val item = items.find { it.producto.id == productoId }
        item?.let {
            if (it.cantidad > 1) {
                it.cantidad--
            } else {
                // Si llega a 0 elimina el item del carrito
                eliminarProducto(productoId)
            }
        }
    }

    // Total de productos distintos en el carrito
    //fun totalProductos(): Int = items.size

    // Total de unidades en el carrito
    fun totalUnidades(): Int = items.sumOf { it.cantidad }

    // Total a pagar
    fun totalPagar(): Double = items.sumOf { it.subtotal() }


    // Obtener cantidad de un producto específico
    fun getCantidad(productoId: String): Int {
        return items.find { it.producto.id == productoId }?.cantidad ?: 0
    }

    // Limpiar carrito después de pagar
    fun limpiarCarrito() {
        items.clear()
    }

    // Verificar si el carrito está vacío
    fun estaVacio(): Boolean = items.isEmpty()
}