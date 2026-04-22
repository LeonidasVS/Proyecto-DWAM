package com.example.shopnova.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopnova.Repository.AuthRepository
import com.example.shopnova.Utils.UiState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableLiveData< UiState< FirebaseUser>>(UiState.Idle)
    val loginState: LiveData<UiState<FirebaseUser>> = _loginState

    private val _registerState = MutableLiveData<UiState<FirebaseUser>>(UiState.Idle)
    val registerState: LiveData<UiState<FirebaseUser>> = _registerState

    fun login(email: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch{
            _loginState.value = repository.login(email, password)
        }
    }

    fun register(name: String, email: String, password: String) {
        _registerState.value = UiState.Loading
        viewModelScope.launch {
            _registerState.value = repository.register(name, email, password)
        }
    }

    fun logout() = repository.logout()

    fun isLoggedIn() = repository.getCurrentUser() != null

    fun resetLoginState() { _loginState.value = UiState.Idle }
    fun resetRegisterState() { _registerState.value = UiState.Idle }
}