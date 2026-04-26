package com.example.shopnova.Utils

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.shopnova.UI.CreateProductActivity
import com.example.shopnova.UI.ProductListActivity

object RolUtils {

    // ── Roles disponibles ─────────────────────────────────────────────────────
    const val ROL_ADMIN   = "administrador"
    const val ROL_CLIENTE = "cliente"

    // ── Formato visual del rol ────────────────────────────────────────────────
    fun formatearRol(rol: String): String {
        return when (rol.lowercase()) {
            ROL_ADMIN   -> "👑 Administrador"
            ROL_CLIENTE -> "🛒 Cliente"
            else        -> "👤 $rol"
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
}