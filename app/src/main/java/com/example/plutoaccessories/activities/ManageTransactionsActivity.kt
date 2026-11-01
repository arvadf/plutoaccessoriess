package com.example.plutoaccessories.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plutoaccessories.adapters.ManageTransactionsAdapter
import com.example.plutoaccessories.databinding.ActivityManageTransactionsBinding
import com.example.plutoaccessories.models.Transaction
import com.example.plutoaccessories.models.Wallet
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageTransactionsBinding
    private lateinit var apiService: ApiService
    private lateinit var adapter: ManageTransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)
        setupRecyclerView()
        fetchTransactions()
    }

    private fun setupRecyclerView() {
        adapter = ManageTransactionsAdapter(emptyList(), this::onAccept, this::onReject)
        binding.rvTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvTransactions.adapter = adapter
    }

    private fun fetchTransactions() {
        apiService.getAllTransactions().enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    adapter.updateTransactions(response.body().orEmpty())
                } else {
                    Toast.makeText(this@ManageTransactionsActivity, "Gagal memuat transaksi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Toast.makeText(this@ManageTransactionsActivity, "Koneksi gagal", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun onAccept(transaction: Transaction) {
        // First, update the wallet balance
        apiService.getWallet(idUserFilter = "eq.${transaction.userId}").enqueue(object : Callback<List<Wallet>> {
            override fun onResponse(call: Call<List<Wallet>>, response: Response<List<Wallet>>) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    val wallet = response.body()!!.first()
                    val newBalance = (wallet.balance ?: 0.0) + transaction.amount
                    apiService.updateWallet(idUserFilter = "eq.${transaction.userId}", body = mapOf("saldo" to newBalance)).enqueue(object: Callback<List<Wallet>> {
                        override fun onResponse(p0: Call<List<Wallet>>, p1: Response<List<Wallet>>) { updateTransactionStatus(transaction.id!!, "Berhasil", null) }
                        override fun onFailure(p0: Call<List<Wallet>>, p1: Throwable) { Toast.makeText(this@ManageTransactionsActivity, "Gagal memperbarui dompet", Toast.LENGTH_SHORT).show() }
                    })
                } else {
                    // If wallet doesn't exist, create it
                    val newWallet = Wallet(userId = transaction.userId, balance = transaction.amount)
                    apiService.createWallet(newWallet).enqueue(object: Callback<List<Wallet>> {
                        override fun onResponse(p0: Call<List<Wallet>>, p1: Response<List<Wallet>>) { updateTransactionStatus(transaction.id!!, "Berhasil", null) }
                        override fun onFailure(p0: Call<List<Wallet>>, p1: Throwable) { Toast.makeText(this@ManageTransactionsActivity, "Gagal membuat dompet", Toast.LENGTH_SHORT).show() }
                    })
                }
            }
            override fun onFailure(call: Call<List<Wallet>>, t: Throwable) {
                Toast.makeText(this@ManageTransactionsActivity, "Gagal memuat dompet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onReject(transaction: Transaction) {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Alasan Penolakan")
            .setView(input)
            .setPositiveButton("Kirim") { _, _ ->
                val reason = input.text.toString()
                updateTransactionStatus(transaction.id!!, "Gagal", reason)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateTransactionStatus(id: Long, status: String, reason: String?) {
        val updates = mutableMapOf<String, Any>("status" to status)
        if (reason != null) {
            updates["rejection_reason"] = reason
        }

        apiService.updateTransaction("eq.$id", updates).enqueue(object : Callback<Transaction> {
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageTransactionsActivity, "Status transaksi diperbarui", Toast.LENGTH_SHORT).show()
                    fetchTransactions()
                } else {
                    Toast.makeText(this@ManageTransactionsActivity, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Toast.makeText(this@ManageTransactionsActivity, "Koneksi gagal", Toast.LENGTH_LONG).show()
            }
        })
    }
}
