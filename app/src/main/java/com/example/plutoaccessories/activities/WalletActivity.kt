package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plutoaccessories.databinding.ActivityWalletBinding
import com.example.plutoaccessories.adapters.TransactionAdapter
import com.example.plutoaccessories.models.Transaction
import com.example.plutoaccessories.models.Wallet
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.FormatHelper
import com.example.plutoaccessories.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)
        sessionManager = SessionManager(this)

        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = TransactionAdapter(emptyList()) // Adapter will be updated after data is received

        binding.btnTopUp.setOnClickListener {
            showTopUpDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchWalletData()
    }

    private fun showTopUpDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Masukkan Jumlah Top Up")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        builder.setView(input)

        builder.setPositiveButton("Lanjutkan") { dialog, _ ->
            val amountString = input.text.toString()
            if (amountString.isNotEmpty()) {
                val amount = amountString.toDouble()
                val intent = Intent(this, QrisPaymentActivity::class.java).apply {
                    putExtra("TOP_UP_AMOUNT", amount)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun fetchWalletData() {
        val userId = sessionManager.userId
        if (userId == -1) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getWallet(idUserFilter = "eq.$userId").enqueue(object : Callback<List<Wallet>> {
            override fun onResponse(call: Call<List<Wallet>>, response: Response<List<Wallet>>) {
                if (response.isSuccessful) {
                    val wallet = response.body()?.firstOrNull()
                    if (wallet != null) {
                        binding.tvBalance.text = FormatHelper.formatRupiah(wallet.balance ?: 0.0)
                        fetchTransactions(userId)
                    } else {
                        binding.tvBalance.text = FormatHelper.formatRupiah(0.0)
                        binding.rvTransactions.adapter = TransactionAdapter(emptyList())
                    }
                } else {
                    Toast.makeText(this@WalletActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Wallet>>, t: Throwable) {
                Toast.makeText(this@WalletActivity, "Koneksi Gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchTransactions(userId: Int) {
        apiService.getTransactions(idUserFilter = "eq.$userId").enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    val transactions = response.body().orEmpty()
                    binding.rvTransactions.adapter = TransactionAdapter(transactions)
                } else {
                    Toast.makeText(this@WalletActivity, "Gagal memuat transaksi: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Toast.makeText(this@WalletActivity, "Koneksi Gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
