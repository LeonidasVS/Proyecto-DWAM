package com.example.shopnova.Model

data class Producto(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
){
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", "", "", 0.0, 0, "", 0L, 0L)

    fun toMap(): Map<String, Any> = mapOf(
        "id"          to id,
        "name"        to name,
        "description" to description,
        "price"       to price,
        "stock"       to stock,
        "category"    to category,
        "createdAt"   to createdAt,
        "updatedAt"   to System.currentTimeMillis()
    )
}

