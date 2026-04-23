package com.example.shopnova.UI

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.example.shopnova.databinding.ActivityCreateProductBinding
import kotlin.getValue

class CreateProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateProductBinding
    private val viewModel: ProductViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        binding= ActivityCreateProductBinding.inflate(layoutInflater)

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

        binding.btnSave.setOnClickListener {
            if (validarCampos()) {
                guardarProducto()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun guardarProducto() {
        val product = Producto(
            name        = binding.etName.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            price       = binding.etPrice.text.toString().toDouble(),
            stock       = binding.etStock.text.toString().toInt(),
            category    = binding.etCategory.text.toString().trim()
        )
        viewModel.createProduct(product)
    }

    private fun setupObservers() {
        viewModel.createState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visible()
                    binding.btnSave.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressBar.gone()
                    showToast(getString(R.string.success_product_created))
                    viewModel.resetCreateState()
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    binding.root.showSnackbarError(state.message)
                    viewModel.resetCreateState()
                }
                else -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                }
            }
        }
    }
    private fun validarCampos(): Boolean {
        var isValid = true
        binding.tilName.error     = null
        binding.tilPrice.error    = null
        binding.tilStock.error    = null
        binding.tilCategory.error = null

        if (!ValidationUtils.isNotEmpty(binding.etName.text.toString())) {
            binding.tilName.error = getString(R.string.field_required); isValid = false
        }
        if (!ValidationUtils.isValidPrice(binding.etPrice.text.toString())) {
            binding.tilPrice.error = getString(R.string.price_invalid); isValid = false
        }
        if (!ValidationUtils.isValidStock(binding.etStock.text.toString())) {
            binding.tilStock.error = getString(R.string.stock_invalid); isValid = false
        }
        if (!ValidationUtils.isNotEmpty(binding.etCategory.text.toString())) {
            binding.tilCategory.error = getString(R.string.field_required); isValid = false
        }
        return isValid
    }
}