package com.example.plutoaccessories

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.content.Intent
import com.bumptech.glide.Glide
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
    private lateinit var tvMaterial: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvStock: TextView
    private lateinit var tvDesc: TextView
    private lateinit var btnAdd: Button
    private lateinit var btnBuy: Button
    private lateinit var btnAddToWishlist: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

        img = findViewById(R.id.imgDetail)
        tvNama = findViewById(R.id.tvNamaDetail)
        tvBrand = findViewById(R.id.tvBrandDetail)
        tvMaterial = findViewById(R.id.tvMaterialDetail)
        tvPrice = findViewById(R.id.tvPriceDetail)
        tvStock = findViewById(R.id.tvStockDetail)
        tvDesc = findViewById(R.id.tvDescDetail)
        btnAdd = findViewById(R.id.btnAddToCart)
        btnBuy = findViewById(R.id.btnBuyNow)
        btnAddToWishlist = findViewById(R.id.btnAddToWishlist)

        val id = intent?.getIntExtra("id_produk", -1) ?: -1
        if (id == -1) {
            Toast.makeText(this, "Produk tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchDetail(id)
    }

    private fun fetchDetail(idProduk: Int) {
        val api = ApiClient.getClient().create(ApiService::class.java)
        api.getProductDetail(idProduk).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    val p = response.body()
                    title = p?.namaProduk ?: "Detail Produk"
                    p?.let { product ->
                        tvNama.text = product.namaProduk
                        tvBrand.text = product.merk ?: "-"
                        tvMaterial.text = product.bahan ?: "-"
                        tvPrice.text = product.harga?.let { h -> FormatHelper.formatRupiah(h) } ?: ""
                        tvStock.text = "Stok: ${product.stok ?: 0}"
                        tvDesc.text = product.deskripsi ?: ""

                        if (!product.gambarUrl.isNullOrBlank()) {
                            Glide.with(this@DetailProductActivity)
                                .load(product.gambarUrl)
                                .placeholder(R.drawable.sample_product)
                                .error(R.drawable.sample_product)
                                .into(img)
                        } else {
                            img.setImageResource(R.drawable.sample_product)
                        }

                        btnAdd.setOnClickListener { _ ->
                            val productId = product.idProduk
                            if (productId == null) {
                                Toast.makeText(this@DetailProductActivity, "ID produk tidak valid", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            val cartItem = CartItem(
                                idProduk = productId,
                                nama = product.namaProduk,
                                harga = product.harga ?: 0.0,
                                jumlah = 1,
                                imageUrl = product.gambarUrl
                            )
                            CartStore.add(cartItem)
                            CartPrefs.save(this@DetailProductActivity)
                            Toast.makeText(this@DetailProductActivity, "Ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                        }

                        btnBuy.setOnClickListener { _ ->
                            val productId = product.idProduk
                            if (productId == null) {
                                Toast.makeText(this@DetailProductActivity, "ID produk tidak valid", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }
                            val cartItem = CartItem(
                                idProduk = productId,
                                nama = product.namaProduk,
                                harga = product.harga ?: 0.0,
                                jumlah = 1,
                                imageUrl = product.gambarUrl
                            )
                            CartStore.add(cartItem)
                            CartPrefs.save(this@DetailProductActivity)
                            startActivity(Intent(this@DetailProductActivity, com.example.plutoaccessories.activities.CartActivity::class.java))
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

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(this@DetailProductActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
