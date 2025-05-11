package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodapp.adapter.AddItemAdapter
import com.example.adminfoodapp.databinding.ActivityAllItemBinding
import com.example.adminfoodapp.model.FoodItem
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AllItemActivity : AppCompatActivity() {
    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }
        fetchItemsFromServer()
    }

    private fun fetchItemsFromServer() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2/get_food/get_all_items.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AllItemActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body?.string()
                println("Dữ liệu trả về: $jsonString")
                val itemList = mutableListOf<FoodItem>()

                try {
                    if (jsonString.isNullOrEmpty()) {
                        runOnUiThread {
                            Toast.makeText(this@AllItemActivity, "Dữ liệu rỗng từ server", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val jsonObject = JSONObject(jsonString)
                    val jsonArray = jsonObject.getJSONArray("items")

                    if (jsonArray.length() == 0) {
                        runOnUiThread {
                            Toast.makeText(this@AllItemActivity, "Không có dữ liệu món ăn", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)

                        val id = if (obj.has("id")) obj.getInt("id") else -1
                        val name = obj.getString("food_name")
                        val price = obj.getString("price")
                        val description = obj.optString("description", "")
                        val ingredients = obj.optString("ingredients", "")
                        val imageUrl = obj.getString("image_url").let {
                            if (it.startsWith("http")) it else BASE_URL + it
                        }

                        itemList.add(
                            FoodItem(
                                id = id,
                                food_name = name,
                                price = price,
                                description = description,
                                ingredients = ingredients,
                                image_url = imageUrl
                            )
                        )
                    }

                    runOnUiThread {
                        val adapter = AddItemAdapter(itemList) { item ->

                            println("ID món ăn: ${item.id}")

                            val intent = Intent(this@AllItemActivity, AddItemActivity::class.java).apply {
                                putExtra("mode", "edit")
                                putExtra("id", item.id.toString())
                                putExtra("name", item.food_name)
                                putExtra("price", item.price)
                                putExtra("description", item.description)
                                putExtra("ingredients", item.ingredients)
                                putExtra("image_url", item.image_url)
                            }
                            startActivity(intent)
                        }
                        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this@AllItemActivity)
                        binding.MenuRecyclerView.adapter = adapter
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@AllItemActivity, "Lỗi xử lý JSON: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    companion object {
        const val BASE_URL = "http://192.168.1.8/get_food/"
    }
}
