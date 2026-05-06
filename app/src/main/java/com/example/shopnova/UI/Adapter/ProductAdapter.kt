package com.shopnova.UI.Adapter

import android.view.LayoutInflater
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

    private var rolActual: String = ""


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
                "electrónica"            -> R.drawable.ic_electronica
                "celulares"              -> R.drawable.ic_electronica
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

            // Actualizar UI con stock disponible en tiempo real
            actualizarStockYBoton(product)

            // Click en botón carrito
            binding.btnAgregarCarrito.setOnClickListener {
                val stockDisponible = stockDisponible(product)
                if (stockDisponible > 0) {
                    onAgregarCarrito(product)
                    // ✅ Actualiza inmediatamente sin esperar
                    actualizarStockYBoton(product)
                }
            }

            binding.root.setOnClickListener { onItemClick(product) }
        }

        // Calcula el stock disponible real descontando lo del carrito
        private fun stockDisponible(product: Producto): Int {
            val cantidadEnCarrito = CarritoManager.getCantidad(product.id)
            return (product.stock - cantidadEnCarrito).coerceAtLeast(0)
        }

        // Actualiza el stock visible y el estado del botón en tiempo real
        private fun actualizarStockYBoton(product: Producto) {
            val disponible = stockDisponible(product)

            // ── Stock visible ─────────────────────────────────────────────────
            binding.tvStock.text = "Stock: $disponible"

            // ── Color del chip según stock disponible ─────────────────────────
            val stockColor = when {
                disponible == 0 -> binding.root.context.getColor(R.color.error_red)
                disponible <= 5 -> binding.root.context.getColor(R.color.warning_orange)
                else            -> binding.root.context.getColor(R.color.accent_green)
            }
            binding.chipStock.setCardBackgroundColor(stockColor)

            // ── Botón carrito ─────────────────────────────────────────────────
            when {
                // Sin stock disponible → ocultar botón
                disponible == 0 -> {
                    binding.btnAgregarCarrito.visibility =
                        android.view.View.GONE
                }
                disponible>0->{
                    binding.btnAgregarCarrito.visibility =
                        android.view.View.VISIBLE
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