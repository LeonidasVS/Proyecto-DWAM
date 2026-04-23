package com.example.shopnova.UI

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.Model.Producto
import com.example.shopnova.R
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.ValidationUtils
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.showSnackbarError
import com.example.shopnova.Utils.showToast
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.ProductViewModel
import com.example.shopnova.databinding.ActivityEditProductBinding
import kotlin.getValue

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private val viewModel: ProductViewModel by viewModels()
    private var productId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        binding= ActivityEditProductBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        cargarDatosDelIntent()
        setupObservers()

        binding.btnUpdate.setOnClickListener {
            if (validarCampos()) {
                actualizarProducto()
            }
        }

        binding.btnDelete.setOnClickListener {
            mostrarDialogoEliminar()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun cargarDatosDelIntent() {
        intent?.let {
            productId = it.getStringExtra("product_id") ?: ""
            binding.etName.setText(it.getStringExtra("product_name") ?: "")
            binding.etDescription.setText(it.getStringExtra("product_description") ?: "")
            binding.etPrice.setText(it.getDoubleExtra("product_price", 0.0).toString())
            binding.etStock.setText(it.getIntExtra("product_stock", 0).toString())
            binding.etCategory.setText(it.getStringExtra("product_category") ?: "")
        }
    }

    private fun actualizarProducto() {
        val product = Producto(
            id          = productId,
            name        = binding.etName.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            price       = binding.etPrice.text.toString().toDouble(),
            stock       = binding.etStock.text.toString().toInt(),
            category    = binding.etCategory.text.toString().trim()
        )
        viewModel.updateProduct(product)
    }

    private fun mostrarDialogoEliminar() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.delete_message))
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteProduct(productId)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun setupObservers() {
        viewModel.updateState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visible()
                    binding.btnUpdate.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressBar.gone()
                    showToast(getString(R.string.success_product_updated))
                    viewModel.resetUpdateState()
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.btnUpdate.isEnabled = true
                    binding.root.showSnackbarError(state.message)
                    viewModel.resetUpdateState()
                }
                else -> {
                    binding.progressBar.gone()
                    binding.btnUpdate.isEnabled = true
                }
            }
        }

        viewModel.deleteState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visible()
                    binding.btnDelete.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressBar.gone()
                    showToast(getString(R.string.success_product_deleted))
                    viewModel.resetDeleteState()
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.btnDelete.isEnabled = true
                    binding.root.showSnackbarError(state.message)
                    viewModel.resetDeleteState()
                }
                else -> {
                    binding.progressBar.gone()
                    binding.btnDelete.isEnabled = true
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        var isValid = true
        binding.tilName.error  = null
        binding.tilPrice.error = null
        binding.tilStock.error = null

        if (!ValidationUtils.isNotEmpty(binding.etName.text.toString())) {
            binding.tilName.error = getString(R.string.field_required); isValid = false
        }
        if (!ValidationUtils.isValidPrice(binding.etPrice.text.toString())) {
            binding.tilPrice.error = getString(R.string.price_invalid); isValid = false
        }
        if (!ValidationUtils.isValidStock(binding.etStock.text.toString())) {
            binding.tilStock.error = getString(R.string.stock_invalid); isValid = false
        }
        return isValid
    }
}