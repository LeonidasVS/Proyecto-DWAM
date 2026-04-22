package com.example.shopnova.Model

data class Producto(
    var id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val existencias: Int = 0,
    val categoria: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
){
    constructor() : this("", "", "", 0.0, 0, "", 0L, 0L)

    fun toMap(): Map<String, Any> = mapOf(
        "nombre" to nombre,
        "descripcion" to descripcion,
        "precio" to precio,
        "existencias" to existencias,
        "categoria" to categoria,
        "createdAt" to createdAt,
        "updatedAt" to System.currentTimeMillis()
    )
}
