package com.example.shopnova.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

    // Referencias de nodos en Realtime Database
    val usersRef    get() = database.getReference("users")
    val productsRef get() = database.getReference("products")

    fun isUserLoggedIn()      = auth.currentUser != null
    fun getCurrentUser()      = auth.currentUser
    fun getCurrentUserId()    = auth.currentUser?.uid ?: ""
    fun getCurrentUserEmail() = auth.currentUser?.email ?: ""
    fun getCurrentUserName()  = auth.currentUser?.displayName ?: "Usuario"
}
