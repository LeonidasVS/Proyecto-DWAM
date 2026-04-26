package com.example.shopnova.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopnova.Model.User
import com.example.shopnova.Repository.AuthRepository
import com.example.shopnova.Utils.UiState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // ── Estados observables ───────────────────────────────────────────────────

    private val _loginState = MutableLiveData<UiState<FirebaseUser>>(UiState.Idle)
    val loginState: LiveData<UiState<FirebaseUser>> = _loginState

    private val _registerState = MutableLiveData<UiState<FirebaseUser>>(UiState.Idle)
    val registerState: LiveData<UiState<FirebaseUser>> = _registerState

    private val _userDataState = MutableLiveData<UiState<User>>(UiState.Idle)
    val userDataState: LiveData<UiState<User>> = _userDataState

    private val _updateUserState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val updateUserState: LiveData<UiState<Unit>> = _updateUserState

    private val _deleteUserState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val deleteUserState: LiveData<UiState<Unit>> = _deleteUserState

    // ── Acciones ──────────────────────────────────────────────────────────────

    // LOGIN
    fun login(email: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            _loginState.value = repository.login(email, password)
        }
    }

    // REGISTRO
    fun register(name: String, email: String, role: String, password: String) {
        _registerState.value = UiState.Loading
        viewModelScope.launch {
            _registerState.value = repository.register(name, email, role,password)
        }
    }

    // LEER datos del usuario desde Realtime Database
    fun loadUserData(uid: String) {
        _userDataState.value = UiState.Loading
        viewModelScope.launch {
            _userDataState.value = repository.getUserData(uid)
        }
    }

    // ACTUALIZAR nombre del usuario
    fun updateUser(uid: String, name: String) {
        _updateUserState.value = UiState.Loading
        viewModelScope.launch {
            _updateUserState.value = repository.updateUser(uid, name)
        }
    }

    // ELIMINAR cuenta del usuario
    fun deleteUser(uid: String) {
        _deleteUserState.value = UiState.Loading
        viewModelScope.launch {
            _deleteUserState.value = repository.deleteUser(uid)
        }
    }

    fun logout()     = repository.logout()
    fun isLoggedIn() = repository.getCurrentUser() != null

    fun resetLoginState()      { _loginState.value      = UiState.Idle }
    fun resetRegisterState()   { _registerState.value   = UiState.Idle }
    fun resetUpdateUserState() { _updateUserState.value = UiState.Idle }
    fun resetDeleteUserState() { _deleteUserState.value = UiState.Idle }
}