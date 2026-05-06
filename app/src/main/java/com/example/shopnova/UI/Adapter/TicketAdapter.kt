package com.example.shopnova.UI.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopnova.Model.CarritoItem
import com.example.shopnova.databinding.ItemTicketBinding

class TicketAdapter(
    private val items: List<CarritoItem>
) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ItemTicketBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class TicketViewHolder(
        private val binding: ItemTicketBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarritoItem) {
            binding.tvNombreTicket.text =
                "${item.producto.name}  x${item.cantidad}"
            binding.tvSubtotalTicket.text =
                "$${String.format("%.2f", item.subtotal())}"
        }
    }
}