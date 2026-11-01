package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.ProductAdapter
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.utils.WishlistStore
import com.google.android.material.bottomnavigation.BottomNavigationView

class WishlistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val wishlist: MutableList<Product> = mutableListOf()
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        recyclerView = findViewById(R.id.recyclerViewWishlist)
        recyclerView.layoutManager = LinearLayoutManager(this)

        wishlist.addAll(WishlistStore.getWishlist())

        productAdapter = ProductAdapter(wishlist) { product ->
            val intent = Intent(this, DetailProductActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id ?: -1L)
            startActivity(intent)
        }
        recyclerView.adapter = productAdapter

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        wishlist.clear()
        wishlist.addAll(WishlistStore.getWishlist())
        productAdapter.notifyDataSetChanged()
        bottomNavigationView.selectedItemId = R.id.nav_wishlist
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == bottomNavigationView.selectedItemId) {
                return@setOnNavigationItemSelectedListener false // Do nothing if already on this screen
            }
            val intent = when (item.itemId) {
                R.id.nav_home -> Intent(this, MainActivity::class.java)
                R.id.nav_cart -> Intent(this, CartActivity::class.java)
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
