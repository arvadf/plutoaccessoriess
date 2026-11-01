package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.CartAdapter
import com.example.plutoaccessories.databinding.ActivityCartBinding
import com.example.plutoaccessories.utils.CartStore
import com.example.plutoaccessories.utils.FormatHelper

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateTotal()

        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        cartAdapter.notifyDataSetChanged()
        updateTotal()
        binding.bottomNavigation.selectedItemId = R.id.nav_cart
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(CartStore.getItems()) {
            updateTotal()
        }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun updateTotal() {
        val total = CartStore.getItems().sumOf { it.harga * it.jumlah }
        binding.tvTotalHarga.text = "Total: ${FormatHelper.formatRupiah(total)}"
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == binding.bottomNavigation.selectedItemId) {
                return@setOnNavigationItemSelectedListener false // Do nothing if already on this screen
            }
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_wishlist -> Intent(this, WishlistActivity::class.java)
                R.id.nav_transactions -> Intent(this, TransactionsActivity::class.java)
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
