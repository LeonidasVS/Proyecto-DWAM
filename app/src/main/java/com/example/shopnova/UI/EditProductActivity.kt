package com.example.shopnova.UI

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.Model.Producto
import com.example.shopnova.R
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.ValidationUtils
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.showSnackbarError
import com.example.shopnova.Utils.showToast
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.ProductViewModel
import com.example.shopnova.databinding.ActivityEditProductBinding

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private val viewModel: ProductViewModel by viewModels()
    private var productId: String = ""

    private val categorias = listOf(
        "Electrónica",
        "Celulares",
        "Computadoras y Laptops",
        "Accesorios",
        "Ropa Hombre",
        "Ropa Mujer",
        "Ropa Niños",
        "Calzado",
        "Joyería",
        "Hogar y Muebles",
        "Cocina y Comedor",
        "Herramientas",
        "Deportes",
        "Fitness",
        "Ropa Deportiva",
        "Libros y Educación",
        "Juguetes",
        "Salud y Belleza",
        "Perfumes",
        "Oficina y Papelería",
        "Otros"
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityEditProductBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rol = intent.getStringExtra("rol") ?: RolUtils.ROL_CLIENTE
        if (rol != RolUtils.ROL_ADMIN) {
            finish() // Si no es admin cierra inmediatamente
            return
        }

        setupToolbar()
        setupCategoryDropdown()
        cargarDatosDelIntent()
        setupObservers()

        binding.btnUpdate.setOnClickListener {
            if (validarCampos()) actualizarProducto()
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

    // Configura el dropdown con la lista de categorías
    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categorias
        )
        binding.actvCategory.setAdapter(adapter)

        binding.actvCategory.setOnClickListener {
            binding.actvCategory.showDropDown()
        }
    }

    // Carga los datos del intent y preselecciona la categoia en el dropdown
    private fun cargarDatosDelIntent() {
        intent?.let {
            productId = it.getStringExtra("product_id") ?: ""
            binding.etName.setText(it.getStringExtra("product_name") ?: "")
            binding.etDescription.setText(it.getStringExtra("product_description") ?: "")
            binding.etPrice.setText(it.getDoubleExtra("product_price", 0.0).toString())
            binding.etStock.setText(it.getIntExtra("product_stock", 0).toString())

            // Preseleccionar la categoría actual en el dropdown
            val categoriaActual = it.getStringExtra("product_category") ?: ""
            binding.actvCategory.setText(categoriaActual, false)
        }
    }

    private fun actualizarProducto() {
        val product = Producto(
            id          = productId,
            name      = binding.etName.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            price      = binding.etPrice.text.toString().toDouble(),
            stock       = binding.etStock.text.toString().toInt(),
            category   = binding.actvCategory.text.toString().trim()
        )
        viewModel.updateProduct(product)
    }

    private fun mostrarDialogoEliminar() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_advertencia)
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
        binding.tilName.error     = null
        binding.tilPrice.error    = null
        binding.tilStock.error    = null
        binding.tilCategory.error = null

        if (!ValidationUtils.isNotEmpty(binding.etName.text.toString())) {
            binding.tilName.error = getString(R.string.field_required)
            isValid = false
        }
        if (!ValidationUtils.isValidPrice(binding.etPrice.text.toString())) {
            binding.tilPrice.error = getString(R.string.price_invalid)
            isValid = false
        }
        if (!ValidationUtils.isValidStock(binding.etStock.text.toString())) {
            binding.tilStock.error = getString(R.string.stock_invalid)
            isValid = false
        }
        if (!ValidationUtils.isNotEmpty(binding.actvCategory.text.toString())) {
            binding.tilCategory.error = getString(R.string.field_required)
            isValid = false
        }

        return isValid
    }
}