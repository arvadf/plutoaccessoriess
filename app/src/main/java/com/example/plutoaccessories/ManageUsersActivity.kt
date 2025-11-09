package com.example.plutoaccessories.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.ManageUsersAdapter
import com.example.plutoaccessories.models.UserModel
import org.json.JSONArray
import org.json.JSONException

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
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.6/pluto_api/get_user.php"

        val request = StringRequest(Request.Method.GET, url, { response ->
            try {
                val jsonArray = JSONArray(response)
                userList.clear()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val idUser = if (obj.has("id_user")) obj.optInt("id_user", -1) else null
                    val idAlt = if (obj.has("id")) obj.optInt("id", -1) else null
                    val user = UserModel(
                        idUser = if (idUser != null && idUser > 0) idUser else null,
                        idAlt = if (idAlt != null && idAlt > 0) idAlt else null,
                        nama = obj.optString("nama", ""),
                        email = obj.optString("email", ""),
                        alamat = obj.optString("alamat", null),
                        noHp = obj.optString("no_hp", null),
                        role = obj.optString("role", "user")
                    )
                    userList.add(user)
                }

                adapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                Toast.makeText(this, "Error parsing JSON: " + response, Toast.LENGTH_SHORT).show()
            }
        }, {
            Toast.makeText(this, "Gagal memuat data pengguna", Toast.LENGTH_SHORT).show()
        })

        queue.add(request)
    }
}
