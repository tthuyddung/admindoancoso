package com.example.foodapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.R
import com.example.adminfoodapp.databinding.ActivityEditUserBinding
import org.json.JSONObject

class EditUserActivity : AppCompatActivity() {
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var btnSave: Button
    private var userId: Int = 0
    private lateinit var binding: ActivityEditUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("user_id", 0)
        binding.edtName.setText(intent.getStringExtra("user_name"))
        binding.edtEmail.setText(intent.getStringExtra("user_email"))

        binding.btnSave.setOnClickListener {
            updateUser()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }


    private fun updateUser() {
        val name = binding.edtName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Tên và email không được để trống", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.1.8/get_food/update_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonObject = JSONObject()
        jsonObject.put("id", userId)
        jsonObject.put("name", name)
        jsonObject.put("email", email)

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                val status = response.getString("status")
                if (status == "success") {
                    Toast.makeText(this, "Thông tin người dùng đã được cập nhật", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi cập nhật người dùng", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }
}
