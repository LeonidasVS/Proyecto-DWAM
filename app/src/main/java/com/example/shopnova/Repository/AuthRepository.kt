package com.example.shopnova.Repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.example.shopnova.Model.User
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.UiState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class AuthRepository {

    private val auth     = FirebaseUtils.auth
    private val usersRef = FirebaseUtils.database.getReference("users")

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    suspend fun login(email: String, password: String): UiState<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user   = result.user
            if (user != null) UiState.Success(user)
            else UiState.Error("Usuario no encontrado")
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al iniciar sesión")
        }
    }

    // ── REGISTRO ──────────────────────────────────────────────────────────────
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): UiState<FirebaseUser> {
        return try {
            // 1. Crear en Firebase Auth
            val result       = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser == null) return UiState.Error("Error al crear usuario")

            // 2. Actualizar displayName en Auth
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 3. Guardar en Realtime Database → /users/{uid}
            val userData = mapOf(
                "uid"       to firebaseUser.uid,
                "name"      to name,
                "email"     to email,
                "role"      to "user",
                "createdAt" to System.currentTimeMillis()
            )
            usersRef.child(firebaseUser.uid).setValue(userData).await()

            UiState.Success(firebaseUser)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al registrarse")
        }
    }

    // ── LEER usuario ──────────────────────────────────────────────────────────
    suspend fun getUserData(uid: String): UiState<User> {
        return suspendCancellableCoroutine { continuation ->
            usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val uid2      = snapshot.child("uid").getValue(String::class.java) ?: ""
                        val name      = snapshot.child("name").getValue(String::class.java) ?: ""
                        val email     = snapshot.child("email").getValue(String::class.java) ?: ""
                        val role      = snapshot.child("role").getValue(String::class.java) ?: "user"
                        val createdAt = snapshot.child("createdAt").getValue(Long::class.java) ?: 0L

                        val user = User(uid2, name, email, role, createdAt)
                        continuation.resume(UiState.Success(user))
                    } else {
                        continuation.resume(UiState.Error("Usuario no encontrado"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(UiState.Error(error.message))
                }
            })
        }
    }

    // ── ACTUALIZAR usuario ────────────────────────────────────────────────────
    suspend fun updateUser(uid: String, name: String): UiState<Unit> {
        return try {
            val updates = HashMap<String, Any>()
            updates["name"] = name
            usersRef.child(uid).updateChildren(updates).await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al actualizar usuario")
        }
    }

    // ── ELIMINAR usuario ──────────────────────────────────────────────────────
    suspend fun deleteUser(uid: String): UiState<Unit> {
        return try {
            usersRef.child(uid).removeValue().await()
            auth.currentUser?.delete()?.await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al eliminar usuario")
        }
    }

    fun logout()         = auth.signOut()
    fun getCurrentUser() = auth.currentUser
}