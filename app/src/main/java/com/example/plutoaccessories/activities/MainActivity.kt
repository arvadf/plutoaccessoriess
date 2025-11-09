package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.DetailProductActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.CategoryAdapter
import com.example.plutoaccessories.adapters.ProductAdapter
import com.example.plutoaccessories.models.Category
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerProducts: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val productList: MutableList<Product> = mutableListOf()
    private val allProducts: MutableList<Product> = mutableListOf()

    private lateinit var recyclerCategories: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categoryList: MutableList<Category> = mutableListOf()

    private lateinit var etSearch: EditText
    private var selectedCategory: String = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val session = SessionManager(this)
        if (!session.isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        recyclerProducts = findViewById(R.id.recyclerViewProducts)
        recyclerProducts.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(productList) { product ->
            val intent = Intent(this, DetailProductActivity::class.java)
            intent.putExtra("id_produk", product.idProduk)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }
        recyclerProducts.adapter = productAdapter
        
        // Add layout animation to RecyclerView
        val layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
        recyclerProducts.layoutAnimation = layoutAnimation

        recyclerCategories = findViewById(R.id.recyclerCategories)
        recyclerCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter = CategoryAdapter(categoryList) { category ->
            selectedCategory = category.name
            filterProducts()
        }
        recyclerCategories.adapter = categoryAdapter

        etSearch = findViewById(R.id.etSearch)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    // Already on home screen
                    true
                }
                R.id.nav_wishlist -> {
                    startActivity(Intent(this, WishlistActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    true
                }
                else -> false
            }
        }

        setupSearch()
        setupCategories()
        fetchProducts()
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { filterProducts() }
        })
    }

    private fun setupCategories() {
        // In a real app, these would come from an API
        categoryList.clear()
        categoryList.add(Category("All", "https://i.imgur.com/q7eYw3d.png"))
        categoryList.add(Category("Watches", "https://i.imgur.com/L79m4t6.png"))
        categoryList.add(Category("Rings", "https://i.imgur.com/s1f3fA5.png"))
        categoryList.add(Category("Necklace", "https://i.imgur.com/3jC3j3f.png"))
        categoryList.add(Category("Bracelet", "https://i.imgur.com/gF0y3jW.png"))
        categoryList.add(Category("Rolex", "https://i.imgur.com/2sXB3b6.png"))
        categoryAdapter.notifyDataSetChanged()
    }

    private fun fetchProducts() {
        val api = ApiClient.getClient().create(ApiService::class.java)
        api.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    allProducts.clear()
                    allProducts.addAll(response.body() ?: emptyList())
                    filterProducts()
                } else {
                    Toast.makeText(this@MainActivity, "Gagal memuat produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun filterProducts() {
        val query = etSearch.text.toString().trim().lowercase()
        val filtered = allProducts.filter { p ->
            val byCategory = selectedCategory == "All" || (p.merk?.lowercase() == selectedCategory.lowercase())
            val byQuery = query.isEmpty() || (p.namaProduk.lowercase().contains(query) ||
                    (p.merk ?: "").lowercase().contains(query) ||
                    (p.bahan ?: "").lowercase().contains(query))
            byCategory && byQuery
        }
        productList.clear()
        productList.addAll(filtered)
        productAdapter.notifyDataSetChanged()
        
        // Re-run layout animation on filter
        recyclerProducts.scheduleLayoutAnimation()
    }
}
