package com.shopnova.UI.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopnova.Model.Producto
import com.example.shopnova.R
import com.example.shopnova.Utils.CarritoManager
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.databinding.ItemProductBinding

class ProductAdapter(
    private val rol: String,
    private val onItemClick: (Producto) -> Unit,
    private val onAgregarCarrito: (Producto) -> Unit
) : ListAdapter<Producto, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Producto) {
            binding.tvProductName.text = product.name
            binding.tvCategory.text    = "🏷️ ${product.category}"
            binding.tvDescription.text = product.description
            binding.tvPrice.text       = "$${String.format("%.2f", product.price)}"

            // Icono según categoría
            binding.ivIcon.setImageResource(when (product.category.lowercase()) {
                "electrónica",
                "celulares",
                "computadoras y laptops" -> R.drawable.ic_electronica
                "accesorios"             -> R.drawable.ic_accesorios
                "ropa hombre"            -> R.drawable.ic_ropa_hombre
                "ropa mujer"             -> R.drawable.ic_ropa_mujer
                "ropa niños"             -> R.drawable.ic_ropa_nino
                "calzado"                -> R.drawable.ic_calzado
                "joyería"                -> R.drawable.ic_joyeria
                "hogar y muebles"        -> R.drawable.ic_hogar
                "cocina y comedor"       -> R.drawable.ic_cocina
                "herramientas"           -> R.drawable.ic_herramientas
                "deportes"               -> R.drawable.ic_deporte
                "fitness"                -> R.drawable.ic_fitness
                "ropa deportiva"         -> R.drawable.ic_ropa_deportiva
                "libros y educación"     -> R.drawable.ic_educacion
                "juguetes"               -> R.drawable.ic_juguetes
                "salud y belleza"        -> R.drawable.ic_salud
                "perfumes"               -> R.drawable.ic_perfumes
                "oficina y papelería"    -> R.drawable.ic_papeleria
                else                     -> R.drawable.ic_otros
            })

            // Actualizar stock y botón según rol
            actualizarStockYBoton(product)

            // Click botón carrito
            binding.btnAgregarCarrito.setOnClickListener {
                if (stockDisponible(product) > 0) {
                    onAgregarCarrito(product)
                    actualizarStockYBoton(product)
                }
            }

            binding.root.setOnClickListener { onItemClick(product) }
        }

        // Stock real descontando lo que ya está en carrito
        private fun stockDisponible(product: Producto): Int {
            val enCarrito = CarritoManager.getCantidad(product.id)
            return (product.stock - enCarrito).coerceAtLeast(0)
        }

        private fun actualizarStockYBoton(product: Producto) {
            val disponible        = stockDisponible(product)
            val cantidadEnCarrito = CarritoManager.getCantidad(product.id)

            // ── Stock visible ─────────────────────────────────────────────────
            binding.tvStock.text = "Stock: $disponible"

            // ── Color chip de stock ───────────────────────────────────────────
            val stockColor = when {
                disponible == 0 -> binding.root.context.getColor(R.color.error_red)
                disponible <= 5 -> binding.root.context.getColor(R.color.warning_orange)
                else            -> binding.root.context.getColor(R.color.accent_green)
            }
            binding.chipStock.setCardBackgroundColor(stockColor)

            // ── Botón carrito solo para CLIENTE ──────────────────────────────
            if (rol != RolUtils.ROL_CLIENTE) {
                // Admin y otros roles NO ven el botón
                binding.btnAgregarCarrito.visibility = View.GONE
                return
            }

            // Lógica del botón para clientes
            when {
                disponible == 0 -> {
                    // Sin stock → ocultar botón
                    binding.btnAgregarCarrito.visibility = View.GONE
                }
                cantidadEnCarrito > 0 -> {
                    // Ya tiene en carrito → verde con cantidad
                    binding.btnAgregarCarrito.visibility = View.VISIBLE
                    binding.btnAgregarCarrito.backgroundTintList =
                        binding.root.context.getColorStateList(R.color.accent_green)
                }
                else -> {
                    // Stock disponible → azul
                    binding.btnAgregarCarrito.visibility = View.VISIBLE
                    binding.btnAgregarCarrito.backgroundTintList =
                        binding.root.context.getColorStateList(R.color.primary_blue)
                }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Producto, newItem: Producto) =
            oldItem == newItem
    }
}