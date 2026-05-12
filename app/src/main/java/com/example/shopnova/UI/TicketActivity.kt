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

//importaciones para el ticket
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.shopnova.Utils.showToast
import java.io.File
import java.io.FileOutputStream

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

        binding.btnGuardarPdf.setOnClickListener {
            generarPDF()
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



    private fun generarPDF() {

        try {

            val pdfDocument = PdfDocument()
            val paint = Paint()

            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas

            var y = 50

            paint.textSize = 18f
            canvas.drawText("Ticket ShopNova", 80f, y.toFloat(), paint)

            y += 40

            paint.textSize = 12f

            canvas.drawText(binding.tvNumeroOrden.text.toString(), 20f, y.toFloat(), paint)
            y += 20

            canvas.drawText(binding.tvFechaHora.text.toString(), 20f, y.toFloat(), paint)
            y += 30

            val items = CarritoManager.getItems()

            for (item in items) {

                canvas.drawText(
                    "${item.producto.name} x${item.cantidad}",
                    20f,
                    y.toFloat(),
                    paint
                )

                y += 20
            }

            y += 20

            canvas.drawText(
                "Total: ${binding.tvTotalPagado.text}",
                20f,
                y.toFloat(),
                paint
            )

            pdfDocument.finishPage(page)

            // Carpeta privada de la app
            val carpeta = File(getExternalFilesDir(null), "ShopNova")

            if (!carpeta.exists()) {
                carpeta.mkdirs()
            }

            val archivo = File(
                carpeta,
                "ticket_${System.currentTimeMillis()}.pdf"
            )

            val outputStream = FileOutputStream(archivo)

            pdfDocument.writeTo(outputStream)

            outputStream.flush()
            outputStream.close()

            pdfDocument.close()

            showToast("PDF guardado:\n${archivo.absolutePath}")

        } catch (e: Exception) {

            e.printStackTrace()

            showToast("Error al generar PDF: ${e.message}")
        }
    }
}