package com.example.adminfoodapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.adminfoodapp.AcceptedActivity
import com.example.adminfoodapp.OutForDeliveryActivity
import com.example.adminfoodapp.PendingOrderActivity
import com.example.adminfoodapp.databinding.PendingOrderItemBinding
import com.example.adminfoodapp.model.Order
import com.example.adminfoodapp.utils.Constants

class PendingOrderAdapter(
    private val orders: ArrayList<Order>,
    private val context: Context
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(orders[position], position)
    }

    inner class PendingOrderViewHolder(private val binding: PendingOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order, position: Int) {
            binding.customerName.text = order.food_name
            binding.pendingOrderQuantity.text = order.count
            Glide.with(context).load(order.imageUrl).into(binding.orderFoodImage)

            binding.acceptButton.text = "Nhận"
            1
            binding.acceptButton.setOnClickListener {
                if (order.state == "pending") {
                    updateOrderState(order.id, "accepted") {
                        val intent = Intent(context, AcceptedActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }

        }

        private fun updateOrderState(orderId: Int, newState: String, onSuccess: () -> Unit) {
            val url = "${Constants.BASE_URL}update_order_state.php"
            val request = object : StringRequest(Method.POST, url,
                { response -> onSuccess() },
                { error -> Toast.makeText(context, "Lỗi cập nhật trạng thái", Toast.LENGTH_SHORT).show() }) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "order_id" to orderId.toString(),
                        "state" to newState
                    )
                }
            }
            Volley.newRequestQueue(context).add(request)
        }
    }
}
