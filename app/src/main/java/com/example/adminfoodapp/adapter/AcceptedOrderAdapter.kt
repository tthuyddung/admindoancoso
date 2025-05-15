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
import com.example.adminfoodapp.OutForDeliveryActivity
import com.example.adminfoodapp.databinding.PendingOrderItemBinding
import com.example.adminfoodapp.model.Order
import com.example.adminfoodapp.utils.Constants

class AcceptedOrderAdapter(
    private val orders: List<Order>,
    private val context: Context
) : RecyclerView.Adapter<AcceptedOrderAdapter.AcceptedOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptedOrderViewHolder {
        val binding = PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AcceptedOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: AcceptedOrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    inner class AcceptedOrderViewHolder(private val binding: PendingOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.customerName.text = order.foodName
            binding.pendingOrderQuantity.text = order.count
            Glide.with(context).load(order.imageUrl).into(binding.orderFoodImage)

            binding.acceptButton.text = "Giao hàng"
            binding.acceptButton.isEnabled = true

            binding.acceptButton.setOnClickListener {
                updateOrderState(order.id, "delivered")
            }
        }

        private fun updateOrderState(orderId: Int, newState: String) {
            val url = "${Constants.BASE_URL}update_order_state.php"

            val requestQueue = Volley.newRequestQueue(context)
            val postRequest = object : StringRequest(
                Method.POST, url,
                { response ->
                    val intent = Intent(context, OutForDeliveryActivity::class.java)
                    context.startActivity(intent)
                },
                { error ->
                    Toast.makeText(context, "Lỗi cập nhật đơn hàng", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["order_id"] = orderId.toString()
                    params["state"] = newState
                    return params
                }
            }

            requestQueue.add(postRequest)
        }

    }
}
