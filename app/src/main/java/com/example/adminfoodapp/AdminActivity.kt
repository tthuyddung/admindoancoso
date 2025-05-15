package com.example.adminfoodapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.adapter.AdminAdapter
import com.example.adminfoodapp.databinding.ActivityAdminBinding
import com.example.adminfoodapp.model.Admin

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val adminList = ArrayList<Admin>()
    private lateinit var adapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AdminAdapter(adminList)
        binding.recyclerViewAdmin.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAdmin.adapter = adapter

        binding.backButton.setOnClickListener { finish() }

        fetchAdmins()
    }

    private fun fetchAdmins() {
        val url = "http://192.168.1.18/get_food/get_admins.php"
        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if (status == "success") {
                    val admins = response.getJSONArray("admins")
                    adminList.clear()
                    for (i in 0 until admins.length()) {
                        val obj = admins.getJSONObject(i)
                        val admin = Admin(
                            id = obj.getInt("id"),
                            owner_name = obj.getString("owner_name"),
                            email_or_phone = obj.getString("email_or_phone")
                        )
                        adminList.add(admin)
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Không có admin nào!", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi mạng: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        queue.add(request)
    }
}
