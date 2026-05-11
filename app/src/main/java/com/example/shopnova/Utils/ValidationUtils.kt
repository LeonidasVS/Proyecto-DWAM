package com.example.shopnova.Utils

object ValidationUtils {
    fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isValidPassword(password: String): Boolean =
        password.length >= 5

    fun isNotEmpty(text: String): Boolean =
        text.trim().isNotEmpty()

    fun isValidPrice(price: String): Boolean =
        try { price.toDouble() > 0 } catch (e: NumberFormatException) { false }

    fun isValidStock(stock: String): Boolean =
        try { stock.toInt() >= 0 } catch (e: NumberFormatException) { false }

    fun isValidName(name: String): Boolean =
        name.length <= 75
}