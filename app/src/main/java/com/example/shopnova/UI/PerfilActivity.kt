package com.example.shopnova.UI

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.R
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Viewmodel.AuthViewModel
import com.example.shopnova.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private val viewModel: AuthViewModel by viewModels()

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

        setupToolbar()
        setupObservers()
        cargarDatosUsuario()

        binding.btnEliminarUser.setOnClickListener {
            Toast.makeText(this, "Eliminar usaurio", Toast.LENGTH_LONG).show()
        }
    }


    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun cargarDatosUsuario() {
        val uid = FirebaseUtils.getCurrentUserId()
        if (uid.isNotEmpty()) {
            viewModel.loadUserData(uid)
        }
    }

    private fun setupObservers() {
        viewModel.userDataState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
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
                }
                is UiState.Error -> {
                    binding.tvNombre.text = FirebaseUtils.getCurrentUserName()
                    binding.tvCorreo.text = FirebaseUtils.getCurrentUserEmail()
                    binding.tvRol.text    = "👤 Usuario"
                    binding.tvFecha.text  = "No disponible"
                    binding.tvAvatar.text = "U"
                }
                else -> {}
            }
        }
    }

    // Convierte timestamp a fecha legible
    private fun formatearFecha(timestamp: Long): String {
        if (timestamp == 0L) return "No disponible"
        val formato = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        return formato.format(Date(timestamp))
    }

}