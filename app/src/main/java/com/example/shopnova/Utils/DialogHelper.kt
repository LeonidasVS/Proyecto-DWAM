package com.example.shopnova.Utils

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog

object DialogHelper {

    // --- DIÁLOGOS PÚBLICOS ---

    fun mostrarConfirmacionCompra(context: Context, monto: String, onConfirmar: () -> Unit) {
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("¡Realizar Pago!")
            .setContentText("¿Confirmas tu compra de $$monto?")
            .setConfirmText("Sí, pagar")
            .setConfirmClickListener { it.dismissWithAnimation(); onConfirmar() }
            .setCancelText("Cancelar")

        pDialog.show()
        aplicarEstilo(pDialog, context)
    }

    fun mostrarConfirmacionLogout(context: Context, onConfirmar: () -> Unit) {
        // CAMBIO: WARNING_TYPE para que salga el icono
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Cerrar Sesión")
            .setContentText("¿Estás seguro que deseas cerrar sesión?")
            .setConfirmText("Sí, salir")
            .setConfirmClickListener { it.dismissWithAnimation(); onConfirmar() }
            .setCancelText("Cancelar")

        pDialog.show()
        aplicarEstilo(pDialog, context)
    }

    fun mostrarConfirmacionBorrarProducto(context: Context, onConfirmar: () -> Unit) {
        // CAMBIO: WARNING_TYPE para icono de advertencia
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("¿Eliminar producto?")
            .setContentText("Esta acción no se puede deshacer.")
            .setConfirmText("Sí, eliminar")
            .setConfirmButtonBackgroundColor(android.graphics.Color.parseColor("#F44336"))
            .setConfirmClickListener { it.dismissWithAnimation(); onConfirmar() }
            .setCancelText("Cancelar")

        pDialog.show()
        aplicarEstilo(pDialog, context)
    }

    fun mostrarConfirmacionEliminarCuenta(context: Context, onConfirmar: () -> Unit) {
        // CAMBIO: ERROR_TYPE para que salga la X roja o WARNING_TYPE
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("¡Eliminar Cuenta!")
            .setContentText("¿Estás seguro que deseas eliminar tu cuenta?")
            .setConfirmText("Sí, eliminar")
            .setConfirmClickListener { it.dismissWithAnimation(); onConfirmar() }
            .setCancelText("Cancelar")

        pDialog.show()
        aplicarEstilo(pDialog, context)
    }

    // --- FUNCIÓN PRIVADA PARA NO REPETIR CÓDIGO ---

    private fun aplicarEstilo(pDialog: SweetAlertDialog, context: Context) {
        // 1. Tamaño de la ventana
        val window = pDialog.window
        if (window != null) {
            val layoutParams = window.attributes
            val displayMetrics = context.resources.displayMetrics
            layoutParams.width = (displayMetrics.widthPixels * 0.85).toInt()
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            window.attributes = layoutParams
        }

        // 2. Tamaños de texto
        pDialog.findViewById<TextView>(cn.pedant.SweetAlert.R.id.title_text)?.textSize = 16f
        pDialog.findViewById<TextView>(cn.pedant.SweetAlert.R.id.content_text)?.textSize = 13f

        // 3. Tamaños de botones
        val btnConfirmar = pDialog.findViewById<Button>(cn.pedant.SweetAlert.R.id.confirm_button)
        val btnCancelar = pDialog.findViewById<Button>(cn.pedant.SweetAlert.R.id.cancel_button)

        btnConfirmar?.textSize = 10f
        btnCancelar?.textSize = 10f
    }
}