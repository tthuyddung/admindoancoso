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
        val url = "http://192.168.1.18/get_food/get_delivered_orders.php"
        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
                val customerNames = ArrayList<String>()
                val statuses = ArrayList<String>()

                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val state = obj.getString("state")
                    if (state == "delivered") {
                        customerNames.add(obj.getString("food_name"))
                        statuses.add(state)
                    }
                }

                val deliveredOrders = ArrayList<Pair<String, String>>()
                for (i in customerNames.indices) {
                    deliveredOrders.add(Pair(customerNames[i], statuses[i]))
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
