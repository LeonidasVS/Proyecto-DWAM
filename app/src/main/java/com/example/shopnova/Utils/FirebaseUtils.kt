package com.example.shopnova.Utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    const val COLLECTION_PRODUCTS = "productos"

    fun isUserLoggedIn() = auth.currentUser != null
    fun getCurrentUser() = auth.currentUser
    fun getCurrentUserId() = auth.currentUser?.uid ?: ""
    fun getCurrentUserEmail() = auth.currentUser?.email ?: ""
    fun getCurrentUserName() = auth.currentUser?.displayName ?: "Usuario"
}