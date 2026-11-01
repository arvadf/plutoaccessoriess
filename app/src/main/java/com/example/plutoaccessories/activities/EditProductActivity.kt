package com.example.plutoaccessories.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class EditProductActivity : AppCompatActivity() {

    private lateinit var ivProductImage: ImageView
    private lateinit var btnUploadImage: Button
    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etProductDescription: EditText
    private lateinit var etProductStok: EditText
    private lateinit var spinnerProductBahan: Spinner
    private lateinit var etProductMerk: EditText
    private lateinit var spinnerProductKategori: Spinner
    private lateinit var etProductWarna: EditText
    private lateinit var etProductBerat: EditText
    private lateinit var btnEditProduct: Button
    private lateinit var btnDeleteProduct: Button

    private var product: Product? = null
    private var imageBase64: String? = null
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        apiService = ApiClient.getClient().create(ApiService::class.java)

        ivProductImage = findViewById(R.id.ivProductImage)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductDescription = findViewById(R.id.etProductDescription)
        etProductStok = findViewById(R.id.etProductStok)
        spinnerProductBahan = findViewById(R.id.spinnerProductBahan)
        etProductMerk = findViewById(R.id.etProductMerk)
        spinnerProductKategori = findViewById(R.id.spinnerProductKategori)
        etProductWarna = findViewById(R.id.etProductWarna)
        etProductBerat = findViewById(R.id.etProductBerat)
        btnEditProduct = findViewById(R.id.btnEditProduct)
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct)

        val productId = intent.getLongExtra("PRODUCT_ID", -1)
        if (productId == -1L) {
            Toast.makeText(this, "ID Produk tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchProductDetails(productId)

        btnUploadImage.setOnClickListener {
            openGallery()
        }

        btnEditProduct.setOnClickListener {
            editProduct()
        }

        btnDeleteProduct.setOnClickListener {
            deleteProduct()
        }
    }

    private fun fetchProductDetails(productId: Long) {
        apiService.getProductDetail("eq.$productId").enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                    product = response.body()!![0]
                    populateUi()
                } else {
                    Toast.makeText(this@EditProductActivity, "Gagal memuat detail produk", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Koneksi gagal", Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    private fun populateUi() {
        val categories = arrayOf("Watches", "Rings", "Necklace", "Bracelet", "Rolex")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerProductKategori.adapter = categoryAdapter

        val materials = arrayOf("perak", "emas", "resin", "premium")
        val materialAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materials)
        spinnerProductBahan.adapter = materialAdapter

        etProductName.setText(product?.namaProduk)
        etProductPrice.setText(product?.harga?.toString() ?: "")
        etProductDescription.setText(product?.deskripsi)
        etProductStok.setText(product?.stok?.toString() ?: "0")
        etProductMerk.setText(product?.merk)
        etProductWarna.setText(product?.warna)
        etProductBerat.setText(product?.berat?.toString() ?: "")

        val currentCategory = product?.kategori
        val categoryPosition = categoryAdapter.getPosition(currentCategory)
        if (categoryPosition >= 0) {
            spinnerProductKategori.setSelection(categoryPosition)
        }

        val currentMaterial = product?.bahan
        val materialPosition = materialAdapter.getPosition(currentMaterial)
        if (materialPosition >= 0) {
            spinnerProductBahan.setSelection(materialPosition)
        }

        val imageSource = when {
            product?.gambarUrl.isNullOrBlank() -> null
            product?.gambarUrl?.startsWith("http") == true || product?.gambarUrl?.startsWith("https") == true -> product?.gambarUrl
            product?.gambarUrl?.startsWith("content://") == true -> null
            product?.gambarUrl?.startsWith("data:") == true -> product?.gambarUrl
            else -> "data:image/jpeg;base64,${product?.gambarUrl}"
        }
        Glide.with(this).load(imageSource).placeholder(R.drawable.ic_category_placeholder).into(ivProductImage)
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            ivProductImage.setImageURI(imageUri)
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    private fun editProduct() {
        val name = etProductName.text.toString().trim()
        val price = etProductPrice.text.toString().trim()
        val description = etProductDescription.text.toString().trim()
        val stok = etProductStok.text.toString().trim()
        val bahan = spinnerProductBahan.selectedItem.toString()
        val merk = etProductMerk.text.toString().trim()
        val kategori = spinnerProductKategori.selectedItem.toString()
        val warna = etProductWarna.text.toString().trim()
        val berat = etProductBerat.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val priceValue = price.toDoubleOrNull()
        if (priceValue == null) {
            Toast.makeText(this, "Harga tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val stokValue = stok.toIntOrNull()
        if (stokValue == null) {
            Toast.makeText(this, "Stok tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val beratValue = berat.toDoubleOrNull()
        if (beratValue == null) {
            Toast.makeText(this, "Berat tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = product?.id
        if (productId == null) {
            Toast.makeText(this, "ID produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val updatePayload = mutableMapOf<String, Any?>(
            "nama_produk" to name,
            "deskripsi" to description,
            "harga" to priceValue,
            "stok" to stokValue,
            "bahan" to bahan,
            "merk" to merk,
            "kategori" to kategori,
            "warna" to warna,
            "berat" to beratValue
        )
        if (!imageBase64.isNullOrEmpty()) {
            updatePayload["gambar"] = imageBase64
        }

        val call = apiService.editProduct(
            idFilter = "eq.$productId",
            product = updatePayload
        )

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProductActivity, "Produk berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductActivity, "Gagal mengubah produk: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteProduct() {
        val productId = product?.id
        if (productId == null) {
            Toast.makeText(this, "ID produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val call = apiService.deleteProduct(idFilter = "eq.$productId")

        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProductActivity, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductActivity, "Gagal menghapus produk: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
