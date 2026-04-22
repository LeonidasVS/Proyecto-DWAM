package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.MainActivity
import com.example.shopnova.R
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Viewmodel.AuthViewModel
import com.example.shopnova.Viewmodel.ProductViewModel
import com.example.shopnova.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val autViewModel: AuthViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()



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
        productViewModel.loadProductCount()

        binding.cardProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        binding.cardAddProduct.setOnClickListener {
            startActivity(Intent(this, CreateProductActivity::class.java))
        }

        binding.cardLogout.setOnClickListener {
            mostrarDialogoCerrarSesion()
        }
    }

    private fun setupUI() {
        val user = FirebaseUtils.getCurrentUser()
        binding.tvUserName.text = user?.displayName ?: user?.email ?: "Usuario"
        binding.tvUserEmail.text = "Admin"
    }

    private fun setupObservers() {
        productViewModel.countState.observe(this) { state ->
            if (state is UiState.Success) {
                binding.tvTotalProducts.text = state.data.toString()
            }
        }
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
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