package com.example.adminfoodapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.adapter.AcceptedOrderAdapter
import com.example.adminfoodapp.databinding.ActivityAcceptedBinding
import com.example.adminfoodapp.model.Order
import com.example.adminfoodapp.utils.Constants
import org.json.JSONException

class AcceptedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAcceptedBinding
    private lateinit var adapter: AcceptedOrderAdapter
    private val acceptedOrders = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceptedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AcceptedOrderAdapter(acceptedOrders, this)
        binding.acceptedRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.acceptedRecyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            finish()
        }
        fetchAcceptedOrders()
    }

    private fun fetchAcceptedOrders() {
        val url = "${Constants.BASE_URL}get_orders_state_admin.php?state=accepted"
        val queue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(url,
            { response ->
                try {
                    acceptedOrders.clear()
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

                        acceptedOrders.add(order)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Toast.makeText(this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
            })

        queue.add(request)
    }
}
