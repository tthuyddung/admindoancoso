package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.databinding.ActivityAddUserBinding
import org.json.JSONObject

class AddUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }


        binding.btnAddUser.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                addUser(name, email)
            } else {
                Toast.makeText(this, "Tên và email không được để trống", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUser(name: String, email: String) {
        val url = "http://192.168.1.8/get_food/add_user.php" // Thay đổi URL nếu cần
        val requestQueue = Volley.newRequestQueue(this)

        val jsonObject = JSONObject()
        jsonObject.put("name", name)
        jsonObject.put("email", email)

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                val status = response.getString("status")
                if (status == "success") {
                    Toast.makeText(this, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show()

                    val intent = Intent()
                    intent.putExtra("user_name", name)  // Truyền tên người dùng mới
                    intent.putExtra("user_email", email)  // Truyền email người dùng mới
                    setResult(RESULT_OK, intent)  // Trả kết quả cho Activity trước đó
                    finish() // Đóng AddUserActivity và quay lại AdminUserActivity
                } else {
                    Toast.makeText(this, "Thêm người dùng thất bại", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi mạng: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

}
