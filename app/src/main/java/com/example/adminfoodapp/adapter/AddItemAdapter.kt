package com.example.adminfoodapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodapp.databinding.ItemItemBinding
import com.example.adminfoodapp.model.FoodItem
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AddItemAdapter(
    private val items: MutableList<FoodItem>,
    private val onEdit: (FoodItem) -> Unit
) : RecyclerView.Adapter<AddItemAdapter.AddItemViewHolder>() {

    private val itemQuantities = mutableListOf<Int>().apply { repeat(items.size) { add(1) } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<FoodItem>) {
        items.clear()
        items.addAll(newItems)
        itemQuantities.clear()
        repeat(newItems.size) { itemQuantities.add(1) }
        notifyDataSetChanged()
    }

    inner class AddItemViewHolder(private val binding: ItemItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val item = items[position]
            val quantity = itemQuantities[position]

            binding.apply {
                foodNameTextView.text = item.food_name
                priceTextView.text = item.price
                quantityTextView.text = quantity.toString()

                Glide.with(root.context)
                    .load(item.image_url)
                    .into(foodImageView)

                minusButton.setOnClickListener { decreaseQuantity(position) }
                plusTextView.setOnClickListener { increaseQuantity(position) }
                deleteButton.setOnClickListener { deleteItem(position) }
                EditButton.setOnClickListener { onEdit(item) }
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.quantityTextView.text = itemQuantities[position].toString()
            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                binding.quantityTextView.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
            val item = items[position]
            val requestBody = FormBody.Builder()
                .add("id", item.id.toString())
                .build()

            val request = Request.Builder()
                .url("http://192.168.1.8/get_food/delete_item.php")
                .post(requestBody)
                .build()

            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    val activity = binding.root.context as? Activity
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body?.string()
                    val activity = binding.root.context as? Activity

                    if (response.isSuccessful && bodyString != null) {
                        val json = JSONObject(bodyString)
                        val success = json.getBoolean("success")
                        val message = json.getString("message")

                        activity?.runOnUiThread {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                items.removeAt(position)
                                itemQuantities.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, items.size)
                            }
                        }
                    } else {
                        activity?.runOnUiThread {
                            Toast.makeText(activity, "Lỗi server: HTTP ${response.code}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    }
}
