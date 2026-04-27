package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopnova.R
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.ProductViewModel
import com.example.shopnova.databinding.ActivityProductListBinding
import com.shopnova.UI.Adapter.ProductAdapter

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    // Recibir el rol desde el intent
    private var rolActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProductListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el rol enviado desde DashboardActivity
        rolActual = intent.getStringExtra("rol") ?: RolUtils.ROL_CLIENTE

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupObservers()
        configurarPorRol()
        viewModel.loadProducts()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, CreateProductActivity::class.java))
        }
    }

    // ── Configura la UI según el rol ──────────────────────────────────────────
    private fun configurarPorRol() {
        when (rolActual) {
            RolUtils.ROL_ADMIN -> {
                // Admin puede agregar productos
                binding.fabAdd.visible()
                binding.fabAdd.isEnabled = true
            }
            RolUtils.ROL_CLIENTE -> {
                // Cliente no puede agregar productos
                binding.fabAdd.gone()
                binding.fabAdd.isEnabled = false
            }
            else -> {
                binding.fabAdd.gone()
                binding.fabAdd.isEnabled = false
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { product ->
            // Solo admin puede editar productos
            if (rolActual == RolUtils.ROL_ADMIN) {
                val intent = Intent(this, EditProductActivity::class.java).apply {
                    putExtra("product_id", product.id)
                    putExtra("product_name", product.name)
                    putExtra("product_description", product.description)
                    putExtra("product_price", product.price)
                    putExtra("product_stock", product.stock)
                    putExtra("product_category", product.category)
                }
                startActivity(intent)
            }
            // Cliente no hace nada al hacer click
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        viewModel.productsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visible()
                    binding.recyclerView.gone()
                    binding.layoutEmpty.gone()
                }
                is UiState.Success -> {
                    binding.progressBar.gone()
                    if (state.data.isEmpty()) {
                        binding.recyclerView.gone()
                        binding.layoutEmpty.visible()
                    } else {
                        binding.layoutEmpty.gone()
                        binding.recyclerView.visible()
                        adapter.submitList(state.data)
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.recyclerView.gone()
                    binding.layoutEmpty.visible()
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
    }
}