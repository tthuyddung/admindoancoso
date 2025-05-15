package com.example.adminfoodapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminfoodapp.databinding.ActivityReceivedBinding
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class ReceivedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceivedBinding
    private val client = OkHttpClient()
    private var currentMessageId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceivedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        fetchLatestMessage()

        binding.button.setOnClickListener {
            val reply = binding.send.text.toString().trim()
            if (currentMessageId != -1 && reply.isNotEmpty()) {
                sendReply(currentMessageId, reply)
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung trả lời.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLatestMessage() {
        val request = Request.Builder()
            .url("http://192.168.1.18/get_food/get_messages.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ReceivedActivity, "Lỗi kết nối!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    try {
                        val jsonArray = JSONArray(responseData)
                        if (jsonArray.length() > 0) {
                            val messageObj = jsonArray.getJSONObject(0)
                            currentMessageId = messageObj.getInt("id")
                            val name = messageObj.getString("name")
                            val message = messageObj.getString("message")

                            runOnUiThread {
                                binding.name.text = name
                                binding.received.text = message
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@ReceivedActivity, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@ReceivedActivity, "Không nhận được dữ liệu!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun sendReply(id: Int, reply: String) {
        val formBody = FormBody.Builder()
            .add("id", id.toString())
            .add("reply", reply)
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.18/get_food/reply_message.php")
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@ReceivedActivity, "Gửi thất bại!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val res = response.body?.string()
                runOnUiThread {
                    if (res != null && res.trim() == "success") {
                        Toast.makeText(this@ReceivedActivity, "Đã trả lời!", Toast.LENGTH_SHORT).show()
                        binding.send.setText("")
                        fetchLatestMessage()
                    } else {
                        Toast.makeText(this@ReceivedActivity, "Gửi thất bại hoặc phản hồi sai định dạng!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
