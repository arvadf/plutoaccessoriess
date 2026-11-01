package com.example.plutoaccessories

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
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class AddProductActivity : AppCompatActivity() {

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
    private lateinit var btnAddProduct: Button

    private var imageBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

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
        btnAddProduct = findViewById(R.id.btnAddProduct)

        val categories = arrayOf("Watches", "Rings", "Necklace", "Bracelet", "Rolex")
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerProductKategori.adapter = categoryAdapter

        val materials = arrayOf("perak", "emas", "resin", "premium")
        val materialAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materials)
        spinnerProductBahan.adapter = materialAdapter

        btnUploadImage.setOnClickListener {
            openGallery()
        }

        btnAddProduct.setOnClickListener {
            addProduct()
        }
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

    private fun addProduct() {
        val name = etProductName.text.toString().trim()
        val price = etProductPrice.text.toString().trim()
        val description = etProductDescription.text.toString().trim()
        val stok = etProductStok.text.toString().trim()
        val bahan = spinnerProductBahan.selectedItem.toString()
        val merk = etProductMerk.text.toString().trim()
        val kategori = spinnerProductKategori.selectedItem.toString()
        val warna = etProductWarna.text.toString().trim()
        val berat = etProductBerat.text.toString().trim()

        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageBase64 == null) {
            Toast.makeText(this, "Semua field dan gambar harus diisi", Toast.LENGTH_SHORT).show()
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

        val productRequest = Product(
            namaProduk = name,
            deskripsi = description,
            harga = priceValue,
            stok = stokValue,
            gambarUrl = imageBase64,
            bahan = bahan,
            merk = merk,
            kategori = kategori,
            warna = warna,
            berat = beratValue
        )

        val api = ApiClient.getClient().create(ApiService::class.java)
        val call = api.addProduct(productRequest)

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddProductActivity, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddProductActivity, "Gagal menambahkan produk: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@AddProductActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
