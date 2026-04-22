package com.example.shopnova.Repository

import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.UiState
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseUtils.auth

    suspend fun login(email: String, password: String): UiState<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { UiState.Success(it) }
                ?: UiState.Error("Usuario no encontrado")
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "Error al iniciar sesión")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): UiState<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdates).await()
                UiState.Success(user)
            } ?: UiState.Error("Error al crear usuario")
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "Error al registrarse")
        }
    }

    fun logout() = auth.signOut()

    fun getCurrentUser() = auth.currentUser
}