package com.example.shopnova.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shopnova.Model.CarritoItem
import com.example.shopnova.R
import com.example.shopnova.databinding.ItemCarritoBinding

class CarritoAdapter(
    private val onAumentar: (CarritoItem) -> Unit,
    private val onDisminuir: (CarritoItem) -> Unit,
    private val onEliminar: (CarritoItem) -> Unit
) : ListAdapter<CarritoItem, CarritoAdapter.CarritoViewHolder>(CarritoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarritoViewHolder(
        private val binding: ItemCarritoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarritoItem) {
            val producto = item.producto

            binding.tvNombre.text        = producto.name
            binding.tvPrecioUnitario.text = "Precio unitario: $${String.format("%.2f", producto.price)}"
            binding.tvCantidad.text      = item.cantidad.toString()
            binding.tvSubtotal.text      = "$${String.format("%.2f", item.subtotal())}"

            // Icono según categoría
            binding.ivIcon.setImageResource(when (producto.category.lowercase()) {
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

            // Deshabilitar btnMas si llega al stock máximo
            binding.btnMas.isEnabled = item.cantidad < producto.stock

            // Deshabilitar btnMenos si es 1 (mínimo)
            binding.btnMenos.isEnabled = item.cantidad > 1

            binding.btnMas.setOnClickListener     { onAumentar(item) }
            binding.btnMenos.setOnClickListener   { onDisminuir(item) }
            binding.btnEliminar.setOnClickListener { onEliminar(item) }
        }
    }

    class CarritoDiffCallback : DiffUtil.ItemCallback<CarritoItem>() {
        override fun areItemsTheSame(oldItem: CarritoItem, newItem: CarritoItem) =
            oldItem.producto.id == newItem.producto.id
        override fun areContentsTheSame(oldItem: CarritoItem, newItem: CarritoItem) =
            oldItem == newItem
    }
}