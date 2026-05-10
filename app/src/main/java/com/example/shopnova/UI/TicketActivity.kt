package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopnova.MainActivity
import com.example.shopnova.R
import com.example.shopnova.Repository.ProductRepository
import com.example.shopnova.Utils.CarritoManager
import com.example.shopnova.Utils.FirebaseUtils
import com.example.shopnova.UI.Adapter.TicketAdapter
import com.example.shopnova.Viewmodel.ProductViewModel
import com.example.shopnova.databinding.ActivityTicketBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val repository = ProductRepository()

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityTicketBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mostrarTicket()
        descontarStock()

        binding.btnVolverInicio.setOnClickListener {
            irInicio()
        }
    }

    private fun mostrarTicket() {
        val items = CarritoManager.getItems()

        // Número de orden único
        val numeroOrden = "SN-${System.currentTimeMillis()}"
        binding.tvNumeroOrden.text = "Orden: #$numeroOrden"

        // Fecha y hora actual
        val formato = SimpleDateFormat(
            "dd 'de' MMMM 'de' yyyy  HH:mm",
            Locale("es", "ES")
        )
        binding.tvFechaHora.text = formato.format(Date())

        // Lista de productos en el ticket
        binding.recyclerTicket.layoutManager = LinearLayoutManager(this)
        binding.recyclerTicket.adapter = TicketAdapter(items)

        // Totales
        binding.tvTotalProductos.text = "${CarritoManager.totalUnidades()} items"
        binding.tvTotalPagado.text    = "$${String.format("%.2f", CarritoManager.totalPagar())}"

        // Datos del cliente
        val user = FirebaseUtils.getCurrentUser()
        binding.tvClienteNombre.text = user?.displayName ?: "Usuario"
        binding.tvClienteCorreo.text = user?.email ?: ""
    }

    // Descuenta el stock de cada producto comprado en Firebase
    private fun descontarStock() {
        val items = CarritoManager.getItems()

        CoroutineScope(Dispatchers.IO).launch {
            for (item in items) {
                val nuevoStock = item.producto.stock - item.cantidad
                val productoActualizado = item.producto.copy(
                    stock = nuevoStock.coerceAtLeast(0) // No puede ser negativo
                )
                repository.updateProduct(productoActualizado)
            }

            // Limpiar carrito después de pagar
            CarritoManager.limpiarCarrito()
        }
    }

    private fun irInicio() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}