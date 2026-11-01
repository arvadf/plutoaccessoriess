package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.OrderAdapter
import com.example.plutoaccessories.adapters.TransactionAdapter
import com.example.plutoaccessories.models.Order
import com.example.plutoaccessories.models.Transaction
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionList: MutableList<Transaction> = mutableListOf()

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: OrderAdapter
    private val orders: MutableList<Order> = mutableListOf()
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        recyclerView = findViewById(R.id.recyclerViewTransactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(transactionList)
        recyclerView.adapter = transactionAdapter

        recycler = findViewById(R.id.rvOrders)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter(orders)
        recycler.adapter = adapter

        val sessionManager = SessionManager(this)
        fetchTransactions(sessionManager.userId)
        loadOrders()

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_transactions
    }

    private fun fetchTransactions(userId: Int) {
        val api = ApiClient.getClient().create(ApiService::class.java)
        api.getTransactions(idUserFilter = "eq.$userId").enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    transactionList.clear()
                    transactionList.addAll(response.body() ?: emptyList())
                    transactionAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@TransactionsActivity, "Gagal memuat transaksi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Toast.makeText(this@TransactionsActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
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
                    Toast.makeText(this@TransactionsActivity, "Gagal memuat riwayat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@TransactionsActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == bottomNavigationView.selectedItemId) {
                return@setOnNavigationItemSelectedListener false // Do nothing if already on this screen
            }
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_wishlist -> Intent(this, WishlistActivity::class.java)
                R.id.nav_cart -> Intent(this, CartActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> null
            }
            intent?.let {
                it.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(it)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}
