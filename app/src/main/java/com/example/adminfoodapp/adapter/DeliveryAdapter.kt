package com.example.adminfoodapp.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodapp.databinding.DeliveryItemBinding
import com.example.adminfoodapp.model.Order

class DeliveryAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<DeliveryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: DeliveryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.customerName.text = order.user
            binding.pendingOrderPrice.text = "Tổng tiền: %.2f $".format(order.total_price)
//            binding.textView8.text = order.state

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
}
