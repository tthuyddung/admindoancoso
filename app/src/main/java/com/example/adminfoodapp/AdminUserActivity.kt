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
import com.example.adminfoodapp.utils.Constants
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
        val url = "${Constants.BASE_URL}get_all_users.php"
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
                            userObj.getInt("id_user"),
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
        val url = "${Constants.BASE_URL}delete_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val jsonObject = JSONObject()
        jsonObject.put("id", user.id_user)

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

    private val EDIT_USER_REQUEST_CODE = 2



    private fun onEditUser(user: User) {
        if (user.id_user == 0) {
            Toast.makeText(this, "ID người dùng không hợp lệ!", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, EditUserActivity::class.java)
        intent.putExtra("user_id", user.id_user)
        intent.putExtra("user_name", user.name)
        intent.putExtra("user_email", user.email)
        startActivityForResult(intent, EDIT_USER_REQUEST_CODE)
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

        // Xử lý cập nhật người dùng
        if (requestCode == EDIT_USER_REQUEST_CODE && resultCode == RESULT_OK) {
            getUsers() // gọi lại API để reload danh sách mới
        }
    }

}
