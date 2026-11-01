package com.example.plutoaccessories.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plutoaccessories.AddProductActivity
import com.example.plutoaccessories.activities.EditProductActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.adapters.ProductAdapter
import com.example.plutoaccessories.databinding.ActivityManageProductsBinding
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)

        setupRecyclerView()

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        fetchProducts()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(productList) { product ->
            showOptionsDialog(product)
        }
        binding.recyclerProducts.apply {
            layoutManager = LinearLayoutManager(this@ManageProductsActivity)
            adapter = productAdapter
        }
    }

    private fun showOptionsDialog(product: Product) {
        val options = arrayOf("Ubah Produk", "Hapus Produk")
        AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> editProduct(product)
                    1 -> confirmDeleteProduct(product)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun editProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id)
        startActivity(intent)
    }

    private fun confirmDeleteProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus produk '${product.namaProduk}'?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteProductFromServer(product)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteProductFromServer(product: Product) {
        val idProduk = product.id
        if (idProduk == null) {
            Toast.makeText(this, "ID produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.deleteProduct(idFilter = "eq.$idProduk").enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageProductsActivity, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchProducts()
                } else {
                    Toast.makeText(this@ManageProductsActivity, "Gagal menghapus produk: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(this@ManageProductsActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchProducts() {
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    productList.clear()
                    productList.addAll(response.body() ?: emptyList())
                    productAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@ManageProductsActivity, "Gagal memuat produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@ManageProductsActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
