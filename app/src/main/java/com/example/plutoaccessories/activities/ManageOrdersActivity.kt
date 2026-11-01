package com.example.plutoaccessories.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plutoaccessories.adapters.ManageOrdersAdapter
import com.example.plutoaccessories.databinding.ActivityManageOrdersBinding
import com.example.plutoaccessories.models.Order
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageOrdersBinding
    private lateinit var apiService: ApiService
    private lateinit var adapter: ManageOrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)
        setupRecyclerView()
        fetchOrders()
    }

    private fun setupRecyclerView() {
        adapter = ManageOrdersAdapter(emptyList(), this::onAccept, this::onReject)
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }

    private fun fetchOrders() {
        apiService.getAllOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    adapter.updateOrders(response.body().orEmpty())
                } else {
                    Toast.makeText(this@ManageOrdersActivity, "Gagal memuat pesanan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@ManageOrdersActivity, "Koneksi gagal", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun onAccept(order: Order) {
        updateOrderStatus(order.id!!, "Selesai")
    }

    private fun onReject(order: Order) {
        updateOrderStatus(order.id!!, "Dibatalkan")
    }

    private fun updateOrderStatus(id: Long, status: String) {
        apiService.updateOrderStatus("eq.$id", mapOf("status_pesanan" to status)).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageOrdersActivity, "Status pesanan diperbarui", Toast.LENGTH_SHORT).show()
                    fetchOrders()
                } else {
                    Toast.makeText(this@ManageOrdersActivity, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@ManageOrdersActivity, "Koneksi gagal", Toast.LENGTH_LONG).show()
            }
        })
    }
}
