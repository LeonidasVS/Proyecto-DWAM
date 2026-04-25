package com.shopnova.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopnova.Model.Producto
import com.example.shopnova.databinding.ItemProductBinding

class ProductAdapter(
    private val onItemClick: (Producto) -> Unit
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
            binding.tvCategory.text = "🏷️ ${product.category}"
            binding.tvDescription.text = product.description
            binding.tvPrice.text = "$${String.format("%.2f", product.price)}"
            binding.tvStock.text = "Stock: ${product.stock}"

            binding.tvIcon.text = when (product.category.lowercase()) {
                "electrónica"                -> "💻"
                "ropa y moda"                -> "👕"
                "alimentos y bebidas"        -> "🍽️"
                "hogar y muebles"            -> "🏠"
                "deportes"                   -> "⚽"
                "libros y educación"         -> "📚"
                "juguetes"                   -> "🧸"
                "salud y belleza"            -> "💄"
                "automóviles"                -> "🚗"
                else                         -> "📦"
            }

            val stockColor = when {
                product.stock <= 5  -> binding.root.context
                    .getColor(com.example.shopnova.R.color.error_red)
                product.stock < 10  -> binding.root.context
                    .getColor(com.example.shopnova.R.color.warning_orange)
                else                -> binding.root.context
                    .getColor(com.example.shopnova.R.color.accent_green)
            }
            binding.chipStock.setCardBackgroundColor(stockColor)

            binding.root.setOnClickListener { onItemClick(product) }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Producto, newItem: Producto) =
            oldItem == newItem
    }
}