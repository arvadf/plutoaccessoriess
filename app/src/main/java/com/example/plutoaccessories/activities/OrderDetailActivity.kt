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

        val orderId = intent.getIntExtra("ORDER_ID", -1)
        if (orderId == -1) {
            Toast.makeText(this, "ID Pesanan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchOrderDetail(orderId)
    }

    private fun fetchOrderDetail(orderId: Int) {
        // Anda perlu membuat endpoint baru di ApiService dan PHP untuk ini
        // Untuk sekarang, kita akan pakai data dummy
        // TODO: Buat endpoint get_order_detail.php?id_order=... 

        // Dummy data for now
        binding.tvOrderId.text = "Order ID: #$orderId"
        binding.tvOrderStatus.text = "Status: Diproses"
        binding.tvOrderDate.text = "Tanggal: 10 Nov 2025"
        binding.tvOrderTotal.text = "Total: Rp 150.000"

        // Dummy items
        addTextViewToShowItems("Produk A (x1) - Rp 50.000")
        addTextViewToShowItems("Produk B (x2) - Rp 100.000")
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
