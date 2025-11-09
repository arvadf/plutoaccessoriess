package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.CheckoutActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.CartAdapter
import com.example.plutoaccessories.models.CartItem
import com.example.plutoaccessories.utils.CartPrefs
import com.example.plutoaccessories.utils.CartStore
import com.example.plutoaccessories.utils.FormatHelper
import com.example.plutoaccessories.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalHarga: TextView
    private lateinit var btnCheckout: Button

    private lateinit var cartAdapter: CartAdapter
    private val cartList = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerViewCart)
        tvTotalHarga = findViewById(R.id.tvTotalHarga)
        btnCheckout = findViewById(R.id.btnCheckout)

        // Load persisted cart
        CartPrefs.load(this)
        cartList.clear()
        cartList.addAll(CartStore.getItems())

        cartAdapter = CartAdapter(cartList) { item ->
            removeFromCart(item)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

        updateTotalHarga()

        btnCheckout.setOnClickListener {
            performCheckout()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_cart
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_wishlist -> {
                    startActivity(Intent(this, WishlistActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    // Already on cart screen
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun removeFromCart(item: CartItem) {
        CartStore.remove(item)
        cartList.remove(item)
        cartAdapter.notifyDataSetChanged()
        updateTotalHarga()
    }

    private fun updateTotalHarga() {
        val total = CartStore.total()
        tvTotalHarga.text = "Total: ${FormatHelper.formatRupiah(total)}"
    }

    private fun performCheckout() {
        if (CartStore.getItems().isEmpty()) {
            Toast.makeText(this, "Keranjang belanja kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val session = SessionManager(this)
        if (!session.isLoggedIn) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Start CheckoutActivity to collect address and other details
        startActivity(Intent(this, CheckoutActivity::class.java))
    }
}
