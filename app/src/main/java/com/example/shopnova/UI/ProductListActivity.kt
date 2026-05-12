package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopnova.R
import com.example.shopnova.Utils.CarritoManager
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.ProductViewModel
import com.example.shopnova.databinding.ActivityProductListBinding
import com.shopnova.UI.Adapter.ProductAdapter
import es.dmoral.toasty.Toasty

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


        rolActual = intent.getStringExtra("rol") ?: RolUtils.ROL_CLIENTE

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupObservers()
        configurarPorRol()
        viewModel.loadProducts()

        binding.btnCarrito.setOnClickListener {
            if (!CarritoManager.estaVacio()) {
                startActivity(Intent(this, CarritoActivity::class.java))
            }
        }
    }


    // Configura la UI segun el rol
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun configurarPorRol() {
        when (rolActual) {
            RolUtils.ROL_ADMIN -> {
                // Admin: FAB visible, sin carrito
                CarritoManager.limpiarCarrito()
                binding.btnCarrito.visibility = android.view.View.GONE
                binding.tvBadgeCarrito.visibility = android.view.View.GONE
            }
            RolUtils.ROL_CLIENTE -> {
                // Cliente: sin FAB, con carrito
                binding.btnCarrito.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            rol           = rolActual,
            onItemClick   = { product ->
                if (rolActual == RolUtils.ROL_ADMIN) {
                    val intent = Intent(this, EditProductActivity::class.java).apply {
                        putExtra("rol",                 rolActual)
                        putExtra("product_id",          product.id)
                        putExtra("product_name",        product.name)
                        putExtra("product_description", product.description)
                        putExtra("product_price",       product.price)
                        putExtra("product_stock",       product.stock)
                        putExtra("product_category",    product.category)
                    }
                    startActivity(intent)
                }
            },
            onAgregarCarrito = { product ->
                CarritoManager.agregarProducto(product)
                actualizarBadgeCarrito()
                adapter.notifyDataSetChanged()
                Toasty.success(
                    this,
                    getString(R.string.add_cart),
                    Toast.LENGTH_SHORT,
                    true
                ).show()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter       = adapter
    }

    // Actualiza el contador del carrito en el toolbar
    private fun actualizarBadgeCarrito() {
        val total = CarritoManager.totalUnidades()
        binding.tvBadgeCarrito.text = total.toString()
        binding.tvBadgeCarrito.visibility =
            if (total > 0) View.VISIBLE else View.GONE
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
        adapter.notifyDataSetChanged()
        viewModel.loadProducts()
        actualizarBadgeCarrito()
    }
}