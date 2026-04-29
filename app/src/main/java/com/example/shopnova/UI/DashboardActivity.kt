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
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Viewmodel.AuthViewModel
import com.example.shopnova.Viewmodel.ProductViewModel
import com.example.shopnova.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val autViewModel: AuthViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private var rolActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityDashboardBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupObservers()

        val uid = FirebaseUtils.getCurrentUserId()
        autViewModel.loadUserData(uid = uid)
        productViewModel.loadProductCount()

        binding.cardProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        binding.cardAddProduct.setOnClickListener {
            startActivity(Intent(this, CreateProductActivity::class.java))
        }

        binding.cardPerfilUsuario.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        binding.cardLogout.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }

        binding.cardProducts.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java).apply {
                putExtra("rol", rolActual) // ✅ Envía el rol
            }
            startActivity(intent)
        }
    }

    private fun setupUI() {
        val user = FirebaseUtils.getCurrentUser()
        binding.tvUserName.text = user?.displayName ?: user?.email ?: "Usuario"
    }

    private fun setupObservers() {

        productViewModel.countState.observe(this) { state ->
            if (state is UiState.Success) {
                binding.tvTotalProducts.text = state.data.toString()
            }
        }

        autViewModel.userDataState.observe(this) { state ->
            when (state) {
                is UiState.Success -> {
                    val user  = state.data
                    rolActual = user.role.lowercase()

                    // Nombre y rol
                    binding.tvUserName.text  = user.name.ifEmpty { FirebaseUtils.getCurrentUserName() }
                    binding.tvUserEmail.text = RolUtils.formatearRol(user.role)

                    // Mostrar u ocultar cards según rol
                    RolUtils.configurarVistas(
                        rol    = rolActual,
                        vistas = mapOf(
                            binding.cardAddProduct    to listOf(RolUtils.ROL_ADMIN),
                            binding.cardProducts      to listOf(RolUtils.ROL_ADMIN, RolUtils.ROL_CLIENTE),
                            binding.cardPerfilUsuario to listOf(RolUtils.ROL_ADMIN, RolUtils.ROL_CLIENTE),
                            binding.cardLogout        to listOf(RolUtils.ROL_ADMIN, RolUtils.ROL_CLIENTE),
                        )
                    )

                    // Cambiar texto del card de productos según rol
                    when (rolActual) {
                        RolUtils.ROL_ADMIN -> {
                            binding.tituloCard.text = "Gestionar Productos"
                            binding.subTitulo.text  = "Ver, crear y editar productos"
                        }
                        RolUtils.ROL_CLIENTE -> {
                            binding.tituloCard.text = "Lista de Productos"
                            binding.subTitulo.text  = "Ver productos disponibles"
                        }
                    }
                }
                is UiState.Error -> {
                    binding.tvUserEmail.text = "👤 Usuario"
                }
                else -> {}
            }
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.logout2)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                autViewModel.logout()
                irLogin()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun irLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        productViewModel.loadProductCount()
    }

}