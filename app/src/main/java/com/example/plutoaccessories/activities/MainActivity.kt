package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.BannerAdapter
import com.example.plutoaccessories.adapters.CategoryAdapter
import com.example.plutoaccessories.adapters.ProductAdapter
import com.example.plutoaccessories.databinding.ActivityMainBinding
import com.example.plutoaccessories.models.Category
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var apiService: ApiService

    private val allProducts = mutableListOf<Product>()
    private var selectedCategory: String = "All"
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var bannerTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        if (!session.isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        apiService = ApiClient.getClient().create(ApiService::class.java)

        setupBanner()
        setupProductRecycler()
        setupCategoryRecycler()
        setupSearch()
        setupBottomNavigation()

        fetchBanners()
        fetchCategories()
        fetchProducts()
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        startBannerAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        stopBannerAutoScroll()
    }

    private fun setupBanner() {
        bannerAdapter = BannerAdapter(emptyList())
        binding.viewPagerBanner.adapter = bannerAdapter
    }

    private fun fetchBanners() {
        val bannerItems = listOf(
            "Diskon hingga 50% untuk semua jam tangan!",
            "Koleksi cincin terbaru telah tiba!",
            "Gratis ongkir untuk pembelian di atas Rp500.000"
        )
        bannerAdapter = BannerAdapter(bannerItems)
        binding.viewPagerBanner.adapter = bannerAdapter

        // Loop the banner
        binding.viewPagerBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == bannerItems.size - 1) {
                    handler.postDelayed({ binding.viewPagerBanner.setCurrentItem(0, false) }, 2000)
                }
            }
        })
    }

    private fun startBannerAutoScroll() {
        bannerTimer = Timer()
        bannerTimer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    var currentItem = binding.viewPagerBanner.currentItem
                    val itemCount = bannerAdapter.itemCount
                    if (itemCount > 0) {
                        currentItem = (currentItem + 1) % itemCount
                        binding.viewPagerBanner.setCurrentItem(currentItem, true)
                    }
                }
            }
        }, 3000, 3000)
    }

    private fun stopBannerAutoScroll() {
        bannerTimer?.cancel()
    }

    private fun setupProductRecycler() {
        productAdapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(this, DetailProductActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id ?: -1L)
            startActivity(intent)
        }
        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = productAdapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.layout_animation_fall_down)
        }
    }

    private fun setupCategoryRecycler() {
        categoryAdapter = CategoryAdapter(mutableListOf()) { category ->
            selectedCategory = category.name
            categoryAdapter.setSelectedCategory(category.name)
            filterProducts()
        }
        binding.recyclerCategories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable { filterProducts() }
                handler.postDelayed(searchRunnable!!, 300) // 300ms debounce
            }
        })
    }

    private fun fetchCategories() {
        val categories = listOf(
            Category("All", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSRI0ow4pbUnyZGodub0Rl8cMXlmxxzmxm2Ww&s"), // Add dummy URLs if needed
            Category("Watches", "https://thumbor.sirclocdn.com/unsafe/500x500/filters:format(webp):quality(80)/https://backoffice.urbanicon.co.id/media/catalog/product/cache/923a709fcafa9d89e4e25272324263a0/a/r/ar60079_1.jpeg"),
            Category("Rings", "https://bestbrilliance.com/cdn/shop/products/J99920RDW04_00002_79f8be5f-f25e-4254-9bc8-c81a3ee11d9d.jpg?v=1646911898&width=2500"),
            Category("Necklace", "https://davina.id/cdn/shop/files/WSN080008W00-DAVINA-Karen-Necklace-4000x4000-1_1080x.jpg?v=1747812699"),
            Category("Bracelet", "https://akn-randb.a-cdn.akinoncloud.com/products/2025/05/26/714882/185a899d-2de4-4c1c-98fc-a3ce2184f39f_size3840x4767_cropCenter.jpg"),
            Category("Rolex", "https://w7.pngwing.com/pngs/10/922/png-transparent-rolex-logo-brand-iceberg-image-file-formats-text-logo.png")
        )
        categoryAdapter.updateData(categories)
    }

    private fun fetchProducts() {
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
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
        val query = binding.etSearch.text.toString().trim().lowercase()
        val filteredList = allProducts.filter { product ->
            val matchesCategory = selectedCategory == "All" || product.kategori?.equals(selectedCategory, ignoreCase = true) == true
            val matchesQuery = query.isEmpty() || product.namaProduk?.lowercase()?.contains(query) == true
            matchesCategory && matchesQuery
        }

        productAdapter.updateProducts(filteredList)
        binding.recyclerViewProducts.scheduleLayoutAnimation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == binding.bottomNavigation.selectedItemId) {
                return@setOnNavigationItemSelectedListener false // Do nothing if already on this screen
            }
            val intent = when (item.itemId) {
                R.id.nav_wishlist -> Intent(this, WishlistActivity::class.java)
                R.id.nav_cart -> Intent(this, CartActivity::class.java)
                R.id.nav_transactions -> Intent(this, TransactionsActivity::class.java)
                R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
                else -> null
            }
            intent?.let {
                it.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                startActivity(it)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out) // Optional: for smooth transition
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}
