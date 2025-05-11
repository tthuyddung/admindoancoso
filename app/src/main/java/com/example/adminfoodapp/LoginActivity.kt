package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.databinding.ActivityLoginBinding
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    val url = "http://192.168.1.8/get_food/admin_login.php"

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val emailOrPhone = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()

            if (emailOrPhone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    Log.d("Volley Response", "Response: $response")

                    try {
                        val jsonObject = JSONObject(response)
                        val status = jsonObject.getString("status")

                        if (status == "success") {
                            val id = jsonObject.getInt("id")
                            Toast.makeText(this, "Đăng nhập thành công! ID: $id", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("admin_id", id)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Đăng nhập thất bại. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Lỗi phân tích dữ liệu JSON", Toast.LENGTH_SHORT).show()
                    }

                },
                Response.ErrorListener { error ->
                    Log.e("VolleyError", "Error: ${error.localizedMessage}")
                    Toast.makeText(this, "Lỗi kết nối: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf(
                        "email" to emailOrPhone,
                        "password" to password
                    )
                }
            }

            val queue = Volley.newRequestQueue(this)
            queue.add(stringRequest)
        }
        binding.donthaveButton.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }

    }
}
