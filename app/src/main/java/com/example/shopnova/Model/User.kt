package com.example.shopnova.Model

data class User( val uid: String = "",
                 val name: String = "",
                 val email: String = "",
                 val role: String = "",
                 val createdAt: Long = System.currentTimeMillis()
){
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", "", 0L)

    fun toMap(): Map<String, Any> = mapOf(
        "uid"       to uid,
        "name"      to name,
        "email"     to email,
        "role"      to role,
        "createdAt" to createdAt
    )
}
