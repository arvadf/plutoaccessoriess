package com.example.plutoaccessories.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.databinding.ActivityOrderDetailBinding
import com.example.plutoaccessories.models.Order
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.FormatHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)

        val orderId = intent.getLongExtra("ORDER_ID", -1)
        if (orderId == -1L) {
            Toast.makeText(this, "ID Pesanan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchOrderDetail(orderId)
    }

    private fun fetchOrderDetail(orderId: Long) {
        apiService.getOrderById(orderFilter = "eq.$orderId").enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val order = response.body()?.firstOrNull()
                    if (order != null) {
                        populateOrderDetails(order)
                    } else {
                        Toast.makeText(this@OrderDetailActivity, "Pesanan tidak ditemukan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@OrderDetailActivity, "Gagal memuat detail pesanan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@OrderDetailActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun populateOrderDetails(order: Order) {
        val orderIdText = order.id?.let { "#$it" } ?: "-"
        binding.tvOrderId.text = "Order ID: $orderIdText"
        binding.tvOrderStatus.text = "Status: ${order.status ?: "-"}"
        binding.tvOrderDate.text = "Tanggal: ${order.createdAt ?: "-"}"
        binding.tvOrderTotal.text = "Total: ${FormatHelper.formatRupiah(order.totalPrice ?: 0.0)}"

        binding.llOrderItems.removeAllViews()
        order.items.orEmpty().forEach { item ->
            val productName = item.product?.namaProduk ?: "Produk ${item.productId}"
            val lineTotal = (item.priceAtOrder ?: 0.0) * item.quantity
            addTextViewToShowItems("$productName (x${item.quantity}) - ${FormatHelper.formatRupiah(lineTotal)}")
        }
    }

    private fun addTextViewToShowItems(itemText: String) {
        val textView = TextView(this).apply {
            text = itemText
            textSize = 16f
            setPadding(0, 8, 0, 8)
        }
        binding.llOrderItems.addView(textView)
    }
}
