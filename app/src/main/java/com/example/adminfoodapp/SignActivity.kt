package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {

    val url = "http://192.168.1.8/get_food/admin.php"

    private val binding : ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val locationList = arrayOf("Ngũ Hành Sơn", "Sơn Trà", "Khánh Hòa", "Liên Chiểu", "Hòa Khánh")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        binding.listOfLocation.setAdapter(adapter)

        binding.alreadyhaveButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.createButton.setOnClickListener {
            val location = binding.listOfLocation.text.toString()
            val owner = binding.editTextTextEmailAddress2.text.toString()
            val restaurant = binding.editTextText.text.toString()
            val emailOrPhone = binding.EmailOrPhone.text.toString()
            val password = binding.SignPassword.text.toString()

            val url = "http://10.0.2.2/get_food/admin.php"

            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    Log.d("Volley Response", "Response: $response")
                    if (response.trim() == "success") {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("VolleyError", "Error: ${error.localizedMessage}")
                    Log.e("VolleyError", "Response: ${String(error.networkResponse?.data ?: byteArrayOf())}")
                    Toast.makeText(this, "Lỗi kết nối: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                    error.printStackTrace() // In lỗi chi tiết ra logcat
                }) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "location" to location,
                        "owner_name" to owner,
                        "restaurant_name" to restaurant,
                        "email_or_phone" to emailOrPhone,
                        "password" to password
                    )
                }
            }


            val queue = Volley.newRequestQueue(this)
            queue.add(stringRequest)
        }
    }

}
