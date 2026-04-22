package com.example.shopnova.Utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.R
import com.google.android.material.snackbar.Snackbar

fun View.visible() { visibility = View.VISIBLE }
fun View.gone() { visibility = View.GONE }

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.showSnackbarError(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setBackgroundTint(
            resources.getColor(com.example.shopnova.R.color.error_red, null)
        )
        .setTextColor(
            resources.getColor(com.example.shopnova.R.color.white, null)
        )
        .show()
}