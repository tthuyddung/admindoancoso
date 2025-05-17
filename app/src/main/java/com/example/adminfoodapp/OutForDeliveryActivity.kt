package com.example.adminfoodapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.adapter.DeliveryAdapter
import com.example.adminfoodapp.databinding.ActivityOutForDeliveryBinding
import com.example.adminfoodapp.model.Order
import com.example.adminfoodapp.utils.Constants
import org.json.JSONArray

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        getDeliveringOrders()
    }

    private fun getDeliveringOrders() {
        val url = "${Constants.BASE_URL}get_delivered_orders.php"
        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
                val deliveredOrders = ArrayList<Order>()

                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val state = obj.getString("state")
                    if (state == "delivered") {

                        val obj = response.getJSONObject(i)

                        val id = obj.getInt("id")
                        val user = obj.getString("user")
                        val foodName = obj.getString("food_name")
                        val count = obj.getString("count")
                        val price = obj.getDouble("total_price")
                        val state = obj.getString("state")
                        val imageUrl = obj.optString("image_url", null) // dùng optString để tránh lỗi nếu thiếu

                        val order = Order(id, user, foodName, count, price, state, imageUrl)
                        deliveredOrders.add(order)


                    }
                }

                val adapter = DeliveryAdapter(deliveredOrders)
                binding.DeliveryRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.DeliveryRecyclerView.adapter = adapter
            },
            {
                Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(request)
    }

}
