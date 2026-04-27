package com.example.shopnova.Utils

import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object RolUtils {

    // ── Roles disponibles ─────────────────────────────────────────────────────
    const val ROL_ADMIN   = "administrador"
    const val ROL_CLIENTE = "cliente"
    const val MAX_ADMINS  = 3

    // ── Formato visual del rol ────────────────────────────────────────────────
    fun formatearRol(rol: String): String {
        return when (rol.lowercase()) {
            ROL_ADMIN   -> "Administrador"
            ROL_CLIENTE -> "Cliente"
            else        -> "$rol"
        }
    }

    // ── Configura visibilidad de views según el rol ───────────────────────────
    fun configurarVistas(rol: String, vistas: Map<View, List<String>>) {
        vistas.forEach { (view, rolesPermitidos) ->
            view.visibility = if (rolesPermitidos.contains(rol.lowercase())) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    // ── Verifica si se puede registrar más administradores ────────────────────
    fun verificarLimiteAdmins(
        onPermitido: () -> Unit,
        onLimitAlcanzado: () -> Unit
    ) {
        val usersRef = FirebaseUtils.database.getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Contar cuántos usuarios tienen rol de administrador
                var contadorAdmins = 0

                for (child in snapshot.children) {
                    val rol = child.child("role").getValue(String::class.java) ?: ""
                    if (rol.lowercase() == ROL_ADMIN) {
                        contadorAdmins++
                    }
                }

                // Verificar si se alcanzó el límite
                if (contadorAdmins >= MAX_ADMINS) {
                    onLimitAlcanzado()
                } else {
                    onPermitido()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Si hay error al consultar, permitir el registro por seguridad
                onPermitido()
            }
        })
    }
}