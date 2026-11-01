package com.example.plutoaccessories.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.ManageUsersAdapter
import com.example.plutoaccessories.models.UserModel
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ManageUsersAdapter
    private val userList = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_users)

        recyclerView = findViewById(R.id.recyclerUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ManageUsersAdapter(userList)
        recyclerView.adapter = adapter

        loadUsers()
    }

    private fun loadUsers() {
        val apiService = ApiClient.getClient().create(ApiService::class.java)
        apiService.getUsers().enqueue(object : Callback<List<UserModel>> {
            override fun onResponse(call: Call<List<UserModel>>, response: Response<List<UserModel>>) {
                if (response.isSuccessful) {
                    userList.clear()
                    userList.addAll(response.body().orEmpty())
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@ManageUsersActivity, "Gagal memuat data pengguna: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UserModel>>, t: Throwable) {
                Toast.makeText(this@ManageUsersActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
