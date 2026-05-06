package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopnova.R
import com.example.shopnova.Utils.CarritoManager
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.visible
import com.example.shopnova.UI.Adapter.CarritoAdapter
import com.example.shopnova.databinding.ActivityCarritoBinding

class CarritoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding
    private lateinit var adapter: CarritoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityCarritoBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupRecyclerView()
        actualizarUI()

        binding.btnPagar.setOnClickListener {
            mostrarDialogoPago()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = CarritoAdapter(
            onAumentar = { item ->
                CarritoManager.aumentarCantidad(item.producto.id)
                actualizarUI()
            },
            onDisminuir = { item ->
                CarritoManager.disminuirCantidad(item.producto.id)
                actualizarUI()
            },
            onEliminar = { item ->
                CarritoManager.eliminarProducto(item.producto.id)
                actualizarUI()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // Actualiza toda la UI del carrito
    private fun actualizarUI() {
        val items = CarritoManager.getItems()

        if (items.isEmpty()) {
            binding.layoutEmpty.visible()
            binding.recyclerView.gone()
            binding.btnPagar.isEnabled = false
        } else {
            binding.layoutEmpty.gone()
            binding.recyclerView.visible()
            binding.btnPagar.isEnabled = true
            adapter.submitList(items.map { it.copy() })
        }

        // Actualizar totales
        binding.tvTotalProductos.text = "${CarritoManager.totalUnidades()} items"
        binding.tvTotal.text = "$${String.format("%.2f", CarritoManager.totalPagar())}"
    }

    private fun mostrarDialogoPago() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_credito)
            .setTitle("¡Realizar Pago!")
            .setMessage("¿Confirmas tu compra de $${String.format("%.2f", CarritoManager.totalPagar())}?")
            .setPositiveButton("Sí, pagar") { _, _ ->
                procesarPago()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun procesarPago() {
        // Ir a TicketActivity
        val intent = Intent(this, TicketActivity::class.java)
        startActivity(intent)
        finish()
    }
}