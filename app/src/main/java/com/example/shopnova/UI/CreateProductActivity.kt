package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.example.shopnova.databinding.ActivityCreateProductBinding
import es.dmoral.toasty.Toasty

class CreateProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProductBinding
    private var rolActual: String = ""

    private val viewModel: ProductViewModel by viewModels()

    // Lista de categorías disponibles
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

        binding = ActivityCreateProductBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rolActual = intent.getStringExtra("rol") ?: RolUtils.ROL_ADMIN

        // Verificar que solo Admin puede crear
        if (rolActual != RolUtils.ROL_ADMIN) {
            finish()
            return
        }

        setupToolbar()
        setupCategoryDropdown()
        setupObservers()

        binding.btnSave.setOnClickListener {
            if (validarCampos()) guardarProducto()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    // Configura el dropdown de categorias
    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categorias
        )
        binding.actvCategory.setAdapter(adapter)

        // Al hacer click abre el dropdown
        binding.actvCategory.setOnClickListener {
            binding.actvCategory.showDropDown()
        }
    }

    private fun guardarProducto() {
        val product = Producto(
            name      = binding.etName.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            price      = binding.etPrice.text.toString().toDouble(),
            stock       = binding.etStock.text.toString().toInt(),
            category   = binding.actvCategory.text.toString().trim()
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
                    Toasty.success(
                        this,
                        getString(R.string.success_product_created),
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    viewModel.resetCreateState()
                    irListaProductos()
                }
                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    Toasty.error(
                        this,
                        getString(R.string.error_product_created),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                    viewModel.resetCreateState()
                }
                else -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                }
            }
        }
    }

    private fun irListaProductos() {
        val intent = Intent(this, ProductListActivity::class.java).apply {
            putExtra("rol", rolActual)
        }
        startActivity(intent)
        finish()
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
        // Validar que se haya seleccionado una categoría
        if (!ValidationUtils.isNotEmpty(binding.actvCategory.text.toString())) {
            binding.tilCategory.error = getString(R.string.field_required)
            isValid = false
        }

        return isValid
    }
}