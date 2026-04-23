package com.example.shopnova.Repository

import com.example.shopnova.Model.Producto
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.UiState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class ProductRepository {

    private val productosRef = FirebaseUtils.database.getReference("productos")

    // ── CREATE ────────────────────────────────────────────────────────────────
    suspend fun createProduct(product: Producto): UiState<String> {
        return try {
            // push() genera ID único automáticamente
            val newRef = productosRef.push()
            val id     = newRef.key ?: return UiState.Error("No se pudo generar ID")

            val data = mapOf(
                "id"          to id,
                "name"      to product.name,
                "description" to product.description,
                "price"      to product.price,
                "stock"       to product.stock,
                "category"   to product.category,
                "createdAt"   to System.currentTimeMillis(),
                "updatedAt"   to System.currentTimeMillis()
            )
            newRef.setValue(data).await()
            UiState.Success(id)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al crear producto")
        }
    }

    // ── READ (todos) ──────────────────────────────────────────────────────────
    suspend fun getProducts(): UiState<List<Producto>> {
        return suspendCancellableCoroutine { continuation ->
            productosRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val productos = mutableListOf<Producto>()

                    for (child in snapshot.children) {
                        val id          = child.child("id").getValue(String::class.java) ?: child.key ?: ""
                        val name      = child.child("name").getValue(String::class.java) ?: ""
                        val description = child.child("description").getValue(String::class.java) ?: ""
                        val price      = child.child("price").getValue(Double::class.java) ?: 0.0
                        val stock       = child.child("stock").getValue(Int::class.java) ?: 0
                        val category   = child.child("category").getValue(String::class.java) ?: ""
                        val createdAt   = child.child("createdAt").getValue(Long::class.java) ?: 0L
                        val updatedAt   = child.child("updatedAt").getValue(Long::class.java) ?: 0L

                        productos.add(
                            Producto(id, name, description, price, stock, category, createdAt, updatedAt)
                        )
                    }

                    // Ordenar por más reciente primero
                    productos.sortByDescending { it.createdAt }
                    continuation.resume(UiState.Success(productos))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(UiState.Error(error.message))
                }
            })
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    suspend fun updateProduct(product: Producto): UiState<Unit> {
        return try {
            if (product.id.isEmpty()) return UiState.Error("ID inválido")

            val updates = HashMap<String, Any>()
            updates["name"]      = product.name
            updates["description"] = product.description
            updates["price"]      = product.price
            updates["stock"]       = product.stock
            updates["category"]   = product.category
            updates["updatedAt"]   = System.currentTimeMillis()

            productosRef.child(product.id).updateChildren(updates).await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al actualizar producto")
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    suspend fun deleteProduct(productId: String): UiState<Unit> {
        return try {
            productosRef.child(productId).removeValue().await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Error al eliminar producto")
        }
    }

    // ── COUNT ─────────────────────────────────────────────────────────────────
    suspend fun getProductCount(): UiState<Int> {
        return suspendCancellableCoroutine { continuation ->
            productosRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(UiState.Success(snapshot.childrenCount.toInt()))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(UiState.Error(error.message))
                }
            })
        }
    }
}