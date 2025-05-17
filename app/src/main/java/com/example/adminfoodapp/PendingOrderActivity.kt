package com.example.adminfoodapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.adapter.PendingOrderAdapter
import com.example.adminfoodapp.databinding.ActivityPendingOrderBinding
import com.example.adminfoodapp.model.Order
import com.example.adminfoodapp.utils.Constants

class PendingOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPendingOrderBinding
    private lateinit var adapter: PendingOrderAdapter
    private val orderList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        adapter = PendingOrderAdapter(orderList, this)
        binding.PendingOrderRecycleView.adapter = adapter
        binding.PendingOrderRecycleView.layoutManager = LinearLayoutManager(this)

        fetchPendingOrders()
    }

    private fun fetchPendingOrders() {
        val state = "pending"
        val url = "${Constants.BASE_URL}get_orders_state_admin.php?state=$state"

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                orderList.clear()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val order = Order(
                        id = obj.getInt("id"),
                        user = obj.getString("user"),
                        food_name = obj.getString("food_name"),
                        count = obj.getString("count"),
                        total_price = obj.getDouble("total_price"),
                        state = obj.getString("state"),
                        imageUrl = Constants.BASE_URL + obj.optString("image_path", "uploads/default.jpg")
                    )


                    orderList.add(order)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Log.e("API_ERROR", error.message ?: "Unknown error")
            })

        Volley.newRequestQueue(this).add(request)
    }
}
