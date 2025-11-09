package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.DetailProductActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.ProductAdapter
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.utils.WishlistStore
import com.google.android.material.bottomnavigation.BottomNavigationView

class WishlistActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val wishlist: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        recyclerView = findViewById(R.id.recyclerViewWishlist)
        recyclerView.layoutManager = LinearLayoutManager(this)

        wishlist.addAll(WishlistStore.getWishlist())

        productAdapter = ProductAdapter(wishlist) { product ->
            val intent = Intent(this, DetailProductActivity::class.java)
            intent.putExtra("id_produk", product.idProduk)
            startActivity(intent)
        }
        recyclerView.adapter = productAdapter

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_wishlist
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_wishlist -> {
                    // Already on wishlist screen
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
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
}
