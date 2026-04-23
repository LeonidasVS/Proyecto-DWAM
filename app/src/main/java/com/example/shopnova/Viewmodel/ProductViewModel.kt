package com.example.shopnova.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopnova.Model.Producto
import com.example.shopnova.Repository.ProductRepository
import com.example.shopnova.Utils.UiState
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    // ── Estados observables ───────────────────────────────────────────────────

    private val _productsState = MutableLiveData<UiState<List<Producto>>>(UiState.Idle)
    val productsState: LiveData<UiState<List<Producto>>> = _productsState

    private val _createState = MutableLiveData<UiState<String>>(UiState.Idle)
    val createState: LiveData<UiState<String>> = _createState

    private val _updateState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val updateState: LiveData<UiState<Unit>> = _updateState

    private val _deleteState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val deleteState: LiveData<UiState<Unit>> = _deleteState

    private val _countState = MutableLiveData<UiState<Int>>(UiState.Idle)
    val countState: LiveData<UiState<Int>> = _countState

    // Cache local para búsqueda sin llamadas extra a Firebase
    private var allProducts: List<Producto> = emptyList()

    // ── Acciones ──────────────────────────────────────────────────────────────

    fun loadProducts() {
        _productsState.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getProducts()
            if (result is UiState.Success) allProducts = result.data
            _productsState.value = result
        }
    }

    // Búsqueda local sin llamadas extra a Firebase
    fun searchProducts(query: String) {
        if (query.isEmpty()) {
            _productsState.value = UiState.Success(allProducts)
            return
        }
        val filtered = allProducts.filter {
            it.name.contains(query, ignoreCase = true)    ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
        _productsState.value = UiState.Success(filtered)
    }

    fun createProduct(product: Producto) {
        _createState.value = UiState.Loading
        viewModelScope.launch {
            _createState.value = repository.createProduct(product)
        }
    }

    fun updateProduct(product: Producto) {
        _updateState.value = UiState.Loading
        viewModelScope.launch {
            _updateState.value = repository.updateProduct(product)
        }
    }

    fun deleteProduct(productId: String) {
        _deleteState.value = UiState.Loading
        viewModelScope.launch {
            _deleteState.value = repository.deleteProduct(productId)
        }
    }

    fun loadProductCount() {
        viewModelScope.launch {
            _countState.value = repository.getProductCount()
        }
    }

    fun resetCreateState() { _createState.value = UiState.Idle }
    fun resetUpdateState() { _updateState.value = UiState.Idle }
    fun resetDeleteState() { _deleteState.value = UiState.Idle }
}