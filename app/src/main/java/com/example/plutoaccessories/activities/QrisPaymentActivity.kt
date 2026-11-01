package com.example.plutoaccessories.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.databinding.ActivityQrisPaymentBinding
import com.example.plutoaccessories.models.Transaction
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.FormatHelper
import com.example.plutoaccessories.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QrisPaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrisPaymentBinding
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private var topUpAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrisPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)
        sessionManager = SessionManager(this)

        topUpAmount = intent.getDoubleExtra("TOP_UP_AMOUNT", 0.0)
        if (topUpAmount <= 0) {
            Toast.makeText(this, "Jumlah Top Up tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvTopUpAmount.text = "Jumlah: ${FormatHelper.formatRupiah(topUpAmount)}"

        binding.btnConfirmPayment.setOnClickListener {
            createTopUpRequest()
        }
    }

    private fun createTopUpRequest() {
        val userId = sessionManager.userId
        if (userId == -1) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionRequest = Transaction(
            userId = userId,
            type = "Top-Up",
            amount = topUpAmount,
            description = "Top up via QRIS",
            status = "Diproses" // Status awal
        )

        apiService.addTransaction(transactionRequest).enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@QrisPaymentActivity, "Permintaan Top Up telah dikirim dan sedang diproses.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@QrisPaymentActivity, "Gagal mengirim permintaan Top Up: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Toast.makeText(this@QrisPaymentActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
