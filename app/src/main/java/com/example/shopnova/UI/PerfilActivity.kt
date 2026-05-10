package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.MainActivity
import com.example.shopnova.R
import com.example.shopnova.Utils.DialogHelper
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.showSnackbarError
import com.example.shopnova.Utils.showToast
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.AuthViewModel
import com.example.shopnova.databinding.ActivityPerfilBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private val viewModel: AuthViewModel by viewModels()
    private var uid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        binding= ActivityPerfilBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        uid = FirebaseUtils.getCurrentUserId()

        setupToolbar()
        setupObservers()
        cargarDatosUsuario()

        binding.btnEliminarUser.setOnClickListener {
            mostrarDialogoEliminar()
        }
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun cargarDatosUsuario() {
        if (uid.isNotEmpty()) {
            viewModel.loadUserData(uid)
        }
    }

    private fun setupObservers() {
        viewModel.userDataState.observe(this) { state ->
            when (state) {

                is UiState.Loading -> {
                    binding.progressBar.visible()
                    binding.tvAvatar.gone()
                    binding.cardUsuario.gone()
                    binding.btnEliminarUser.gone()
                }

                is UiState.Success -> {

                    binding.progressBar.gone()
                    binding.tvAvatar.visible()
                    binding.cardUsuario.visible()
                    binding.btnEliminarUser.visible()

                    val user = state.data

                    // Nombre
                    binding.tvNombre.text = user.name.ifEmpty { "Sin nombre" }

                    // Correo
                    binding.tvCorreo.text = user.email.ifEmpty { "Sin correo" }

                    // Rol formateado
                    binding.tvRol.text = RolUtils.formatearRol(user.role)

                    // Fecha de registro formateada
                    binding.tvFecha.text = formatearFecha(user.createdAt)

                    // Inicial del nombre en el avatar
                    binding.tvAvatar.text = user.name
                        .firstOrNull()
                        ?.uppercase()
                        ?: "U"

                    // Nombre en el header
                    binding.tvNombreHeader.text = user.name.ifEmpty { "Usuario" }
                }

                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.cardUsuario.visible()
                    binding.btnEliminarUser.visible()
                    binding.tvAvatar.visible()

                    binding.tvNombre.text       = FirebaseUtils.getCurrentUserName()
                    binding.tvCorreo.text       = FirebaseUtils.getCurrentUserEmail()
                    binding.tvRol.text          = "👤 Usuario"
                    binding.tvFecha.text        = "No disponible"
                    binding.tvAvatar.text       = "U"
                    binding.tvNombreHeader.text = FirebaseUtils.getCurrentUserName()
                }

                else -> {}
            }

            viewModel.deleteUserState.observe(this) { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visible()
                        binding.btnEliminarUser.isEnabled = false
                    }
                    is UiState.Success -> {
                        viewModel.resetDeleteUserState()
                        irLogin()
                    }
                    is UiState.Error -> {
                        binding.progressBar.gone()
                        binding.btnEliminarUser.isEnabled = true
                        binding.cardUsuario.visible()
                        binding.root.showSnackbarError(state.message)
                        viewModel.resetDeleteUserState()
                    }
                    else -> {
                        binding.progressBar.gone()
                        binding.btnEliminarUser.isEnabled = true
                    }
                }
            }
        }
    }

    // Convierte timestamp a fecha legible
    private fun formatearFecha(timestamp: Long): String {
        if (timestamp == 0L) return "No disponible"
        val formato = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        return formato.format(Date(timestamp))
    }

    private fun mostrarDialogoEliminar() {
       // Llamamos a nuestra utilidad
        DialogHelper.mostrarConfirmacionEliminarCuenta(
            this,
            {
               eliminarUsuario()
            }
        )
    }

    private fun eliminarUsuario(){
        if (uid.isEmpty()) return
        viewModel.deleteUser(uid)
    }

    private fun irLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}