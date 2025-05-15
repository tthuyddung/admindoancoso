package com.example.adminfoodapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.databinding.ActivityEditProfileBinding
import org.json.JSONObject

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var id: String? = null

    private val getUrl = "http://192.168.1.8/get_food/get_profile.php"
    private val updateUrl = "http://192.168.1.8/get_food/update_profile.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("AdminSession", MODE_PRIVATE)
        id = sharedPref.getInt("admin_id", -1).takeIf { it != -1 }?.toString()

        if (id == null) {
            Toast.makeText(this, "Không tìm thấy ID admin!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        getUserProfile()

        binding.btnSave.setOnClickListener {
            updateUserProfile()
        }
        binding.backButton.setOnClickListener{
            finish()
        }
    }

    private fun getUserProfile() {
        val request = object : StringRequest(Method.POST, getUrl,
            { response ->
                val json = JSONObject(response)
                if (json.getString("status") == "success") {
                    val user = json.getJSONObject("data")
                    binding.etLocation.setText(user.getString("location"))
                    binding.etOwner.setText(user.getString("owner_name"))
                    binding.etRestaurant.setText(user.getString("restaurant_name"))
                    binding.etEmail.setText(user.getString("email_or_phone"))
                    binding.etPassword.setText(user.getString("password"))
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf("id" to id!!)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }

    private fun updateUserProfile() {
        val location = binding.etLocation.text.toString()
        val owner = binding.etOwner.text.toString()
        val restaurant = binding.etRestaurant.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        val request = object : StringRequest(Method.POST, updateUrl,
            { response ->
                val json = JSONObject(response)
                if (json.getString("status") == "success") {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "id" to id!!,
                    "location" to location,
                    "owner_name" to owner,
                    "restaurant_name" to restaurant,
                    "email_or_phone" to email,
                    "password" to password
                )
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
