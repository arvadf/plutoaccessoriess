package com.example.plutoaccessories.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.OrderAdapter
import com.example.plutoaccessories.models.Order
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: OrderAdapter
    private val orders: MutableList<Order> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        recycler = findViewById(R.id.rvOrders)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter(orders)
        recycler.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        val session = SessionManager(this)
        val id = session.userId
        if (id <= 0) {
            Toast.makeText(this, "Silakan login", Toast.LENGTH_SHORT).show()
            return
        }
        val api = ApiClient.getClient().create(ApiService::class.java)
        api.getOrders(idUserFilter = "eq.$id").enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    orders.clear()
                    orders.addAll(response.body().orEmpty())
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@OrderHistoryActivity, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@OrderHistoryActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
