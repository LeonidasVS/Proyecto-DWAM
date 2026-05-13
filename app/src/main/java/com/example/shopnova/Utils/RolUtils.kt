package com.example.shopnova.Utils

import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object RolUtils {

    // Roles disponibles
    const val ROL_ADMIN   = "administrador"
    const val ROL_CLIENTE = "cliente"

    // Formato visual del rol
    fun formatearRol(rol: String): String {
        return when (rol.lowercase()) {
            ROL_ADMIN   -> "Admin"
            ROL_CLIENTE -> "Cliente"
            else        -> "$rol"
        }
    }

    // Configura visibilidad de views según el rol
    fun configurarVistas(rol: String, vistas: Map<View, List<String>>) {
        vistas.forEach { (view, rolesPermitidos) ->
            view.visibility = if (rolesPermitidos.contains(rol.lowercase())) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}