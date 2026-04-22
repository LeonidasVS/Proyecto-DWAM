package com.example.shopnova.Repository

import com.example.shopnova.Model.Producto
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.UiState
import com.google.firebase.database.Query
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val collection = FirebaseUtils.firestore
        .collection(FirebaseUtils.COLLECTION_PRODUCTS)

    suspend fun createProduct(product: Producto): UiState<String> {
        return try {
            val docRef = collection.add(product.toMap()).await()
            UiState.Success(docRef.id)
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "Error al crear producto")
        }
    }

    suspend fun getProducts(): UiState<List<Producto>> {
        return try {
            val snapshot = collection
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Producto::class.java)?.apply { id = doc.id }
            }
            UiState.Success(products)
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "Error al obtener productos")
        }
    }

    suspend fun updateProduct(product: Producto): UiState<Unit> {
        return try {
            collection.document(product.id).update(product.toMap()).await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "Error al actualizar")
        }
    }

    suspend fun deleteProduct(productId: String): UiState<Unit> {
        return try {
            collection.document(productId).delete().await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "Error al eliminar")
        }
    }

    suspend fun getProductCount(): UiState<Int> {
        return try {
            val snapshot = collection.get().await()
            UiState.Success(snapshot.size())
        } catch (e: Exception) {
            UiState.Error(e.localizedMessage ?: "Error")
        }
    }
}