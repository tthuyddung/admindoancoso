package com.example.adminfoodapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.adminfoodapp.databinding.ActivityAddItemBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import android.util.Log
import com.example.adminfoodapp.utils.Constants
import java.io.IOException

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding
    private var imageUri: Uri? = null
    private var mode: String? = null
    private var itemId: String? = null // Lưu ID khi sửa
    private var oldImageUrl: String? = null // Dùng nếu bạn muốn giữ ảnh cũ khi không chọn ảnh mới

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getStringExtra("mode")
        if (mode == "edit") {
            itemId = intent.getStringExtra("id")
            Log.d("AddItemActivity", "Item ID: $itemId")

            val name = intent.getStringExtra("name")
            val price = intent.getStringExtra("price")
            val description = intent.getStringExtra("description")
            val ingredients = intent.getStringExtra("ingredients")
            oldImageUrl = intent.getStringExtra("image_url")

            binding.enterFoodName.setText(name)
            binding.enterFoodPrice.setText(price)
            binding.description.setText(description)
            binding.ingredint.setText(ingredients)

            if (!oldImageUrl.isNullOrEmpty()) {
                Glide.with(this).load(oldImageUrl).into(binding.selectedImage)
            }
            binding.AddItemButton.text = "Cập nhật món"
        } else {
            binding.selectedImage.setImageResource(R.drawable.menu1)
        }

        binding.selectedImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        binding.AddItemButton.setOnClickListener {
            uploadData()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.selectedImage.setImageURI(imageUri)
        }
    }

    private fun uploadData() {
        val foodName = binding.enterFoodName.text.toString().trim()
        val price = binding.enterFoodPrice.text.toString().trim()
        val description = binding.description.text.toString().trim()
        val ingredients = binding.ingredint.text.toString().trim()

        if (foodName.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (mode == "edit") {
            if (itemId.isNullOrEmpty() || itemId == "-1") {
                Toast.makeText(this, "ID món ăn không hợp lệ. Không thể cập nhật món.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val contentResolver = contentResolver
        val tempFile: File

        if (imageUri == null) {
            val placeholderUri = Uri.parse("android.resource://$packageName/${R.drawable.menu1}")
            val inputStream = contentResolver.openInputStream(placeholderUri)
            tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
        } else {
            val inputStream = contentResolver.openInputStream(imageUri!!)
            tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }
        }

        val url = if (mode == "edit") {
            "${Constants.BASE_URL}update_item.php"
        } else {
            "${Constants.BASE_URL}add_item.php"
        }

        val client = OkHttpClient()
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"  // Tên file duy nhất theo thời gian
        val requestBodyBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("food_name", foodName)
            .addFormDataPart("price", price)
            .addFormDataPart("description", description)
            .addFormDataPart("ingredients", ingredients)
            .addFormDataPart("image", fileName, RequestBody.create("image/*".toMediaTypeOrNull(), tempFile))

        if (mode == "edit" && !itemId.isNullOrEmpty()) {
            requestBodyBuilder.addFormDataPart("id", itemId!!)
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBodyBuilder.build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AddItemActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body
                val responseString = responseBody?.string()
                val responseCode = response.code

                if (responseCode == 200) {
                    handleResponse(responseString)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@AddItemActivity, "Lỗi server: HTTP $responseCode", Toast.LENGTH_SHORT).show()
                    }
                }

                responseBody?.close()
            }
        })
    }

    private fun handleResponse(response: String?) {
        try {
            val jsonResponse = JSONObject(response)
            val success = jsonResponse.getBoolean("success")
            val message = jsonResponse.getString("message")

            Log.d("AddItemActivity", "Response JSON: $response")

            runOnUiThread {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                if (success) {
                    val intent = Intent(this, AllItemActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } catch (e: Exception) {
            Log.e("AddItemActivity", "Error parsing response: ${e.message}")
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this, "Lỗi khi giải mã phản hồi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
