package com.example.foodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.adminfoodapp.adapter.UserAdapter
import com.example.adminfoodapp.databinding.ActivityAdminUserBinding
import com.example.adminfoodapp.model.User
import org.json.JSONObject

class AdminUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminUserBinding
    private lateinit var userList: ArrayList<User>
    private lateinit var userAdapter: UserAdapter

    private val ADD_USER_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userList = ArrayList()

        userAdapter = UserAdapter(userList,
            onDelete = { user -> onDeleteUser(user) },
            onEdit = { user -> onEditUser(user) }
        )

        binding.backButton.setOnClickListener{
            finish()
        }

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter = userAdapter

        binding.btnAddUser.setOnClickListener {
            val intent = Intent(this, AddUserActivity::class.java)
            startActivityForResult(intent, ADD_USER_REQUEST_CODE)
        }

        getUsers()
    }

    private fun getUsers() {
        val url = "http://192.168.1.8/get_food/get_all_users.php"
        val requestQueue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val status = response.getString("status")
                if (status == "success") {
                    val users = response.getJSONArray("users")

                    userList.clear()  // <-- Đảm bảo không bị thêm trùng
                    for (i in 0 until users.length()) {
                        val userObj = users.getJSONObject(i)
                        val user = User(
                            userObj.getInt("id"),
                            userObj.getString("name"),
                            userObj.getString("email")
                        )
                        userList.add(user)
                    }
                    userAdapter.setUsers(userList)
                } else {
                    Toast.makeText(this, "Không thể tải người dùng", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi mạng: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

    private fun onDeleteUser(user: User) {
        val url = "http://192.168.1.8/get_food/delete_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonObject = JSONObject()
        jsonObject.put("id", user.id)

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                val status = response.getString("status")
                if (status == "success") {
                    userList.remove(user)
                    userAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Người dùng đã bị xóa", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Lỗi xóa người dùng", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(request)
    }

    private fun onEditUser(user: User) {
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("user_id", user.id)
        intent.putExtra("user_name", user.name)
        intent.putExtra("user_email", user.email)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            val userName = data?.getStringExtra("user_name")
            val userEmail = data?.getStringExtra("user_email")

            if (userName != null && userEmail != null) {
                val newUser = User(0, userName, userEmail)
                userList.add(newUser)
                userAdapter.notifyDataSetChanged()

                Toast.makeText(this, "Thêm người dùng: $userName, $userEmail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
