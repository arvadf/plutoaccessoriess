package com.example.plutoaccessories.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.content.Intent
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.models.CartItem
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.FormatHelper
import com.example.plutoaccessories.utils.CartStore
import com.example.plutoaccessories.utils.CartPrefs
import com.example.plutoaccessories.utils.WishlistStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProductActivity : AppCompatActivity() {
    private lateinit var img: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvBrand: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvStock: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvKategori: TextView
    private lateinit var tvBerat: TextView
    private lateinit var tvWarna: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnBuy: Button
    private lateinit var btnAddToWishlist: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

        img = findViewById(R.id.imgDetail)
        tvNama = findViewById(R.id.tvNamaDetail)
        tvBrand = findViewById(R.id.tvBrandDetail)
        tvPrice = findViewById(R.id.tvPriceDetail)
        tvStock = findViewById(R.id.tvStockDetail)
        tvDesc = findViewById(R.id.tvDescDetail)
        tvKategori = findViewById(R.id.tvKategoriDetail)
        tvBerat = findViewById(R.id.tvBeratDetail)
        tvWarna = findViewById(R.id.tvWarnaDetail)
        btnAdd = findViewById(R.id.btnAddToCart)
        btnBuy = findViewById(R.id.btnBuyNow)
        btnAddToWishlist = findViewById(R.id.btnAddToWishlist)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val id = intent?.getLongExtra("PRODUCT_ID", -1) ?: -1
        if (id == -1L) {
            Toast.makeText(this, "Produk tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchDetail(id.toInt())
    }

    private fun fetchDetail(idProduk: Int) {
        val api = ApiClient.getClient().create(ApiService::class.java)
        api.getProductDetail("eq.$idProduk").enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val p = response.body()?.firstOrNull()
                    title = p?.namaProduk ?: "Detail Produk"
                    p?.let { product ->
                        tvNama.text = product.namaProduk
                        tvBrand.text = product.merk ?: "-"
                        tvPrice.text = product.harga?.let { h -> FormatHelper.formatRupiah(h) } ?: ""
                        tvStock.text = product.stok?.toString() ?: "0"
                        tvDesc.text = product.deskripsi ?: ""
                        tvKategori.text = product.kategori ?: "-"
                        tvBerat.text = "${product.berat?.toString() ?: "-"} gram"
                        tvWarna.text = product.warna ?: "-"
                        val finalImage = when {
                            product.gambarUrl.isNullOrBlank() -> null
                            product.gambarUrl.startsWith("http") || product.gambarUrl.startsWith("https") -> product.gambarUrl
                            product.gambarUrl.startsWith("content://") -> null
                            product.gambarUrl.startsWith("data:") -> product.gambarUrl
                            else -> "data:image/jpeg;base64,${product.gambarUrl}"
                        }

                        Glide.with(this@DetailProductActivity)
                            .load(finalImage)
                            .placeholder(R.drawable.ic_category_placeholder)
                            .error(R.drawable.ic_category_placeholder)
                            .into(img)

                        if (product.stok ?: 0 <= 0) {
                            btnAdd.text = "Stok Habis"
                            btnBuy.text = "Stok Habis"
                            btnAdd.isEnabled = false
                            btnBuy.isEnabled = false
                        } else {
                            btnAdd.setOnClickListener { _ ->
                                val productId = product.id
                                if (productId == null) {
                                    Toast.makeText(this@DetailProductActivity, "ID produk tidak valid", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }
                                val cartItem = CartItem(
                                    idProduk = productId.toInt(),
                                    nama = product.namaProduk ?: "",
                                    harga = product.harga ?: 0.0,
                                    jumlah = 1,
                                    imageUrl = product.gambarUrl
                                )
                                CartStore.add(cartItem)
                                CartPrefs.save(this@DetailProductActivity)
                                Toast.makeText(this@DetailProductActivity, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                            }

                            btnBuy.setOnClickListener { _ ->
                                val productId = product.id
                                if (productId == null) {
                                    Toast.makeText(this@DetailProductActivity, "ID produk tidak valid", Toast.LENGTH_SHORT).show()
                                    return@setOnClickListener
                                }
                                val cartItem = CartItem(
                                    idProduk = productId.toInt(),
                                    nama = product.namaProduk ?: "",
                                    harga = product.harga ?: 0.0,
                                    jumlah = 1,
                                    imageUrl = product.gambarUrl
                                )
                                CartStore.add(cartItem)
                                CartPrefs.save(this@DetailProductActivity)
                                startActivity(Intent(this@DetailProductActivity, com.example.plutoaccessories.activities.CartActivity::class.java))
                            }
                        }

                        btnAddToWishlist.setOnClickListener { _ ->
                            WishlistStore.add(product)
                            Toast.makeText(this@DetailProductActivity, "Ditambahkan ke wishlist", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@DetailProductActivity, "Gagal memuat detail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@DetailProductActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
