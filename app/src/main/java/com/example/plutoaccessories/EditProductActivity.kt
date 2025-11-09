package com.example.plutoaccessories

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.plutoaccessories.models.ApiResponse
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
    private lateinit var btnEditProduct: Button
    private lateinit var btnDeleteProduct: Button

    private var product: Product? = null
    private var imageBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        ivProductImage = findViewById(R.id.ivProductImage)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        etProductName = findViewById(R.id.etProductName)
        etProductPrice = findViewById(R.id.etProductPrice)
        etProductDescription = findViewById(R.id.etProductDescription)
        btnEditProduct = findViewById(R.id.btnEditProduct)
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct)

        product = intent.getParcelableExtra("product")

        if (product == null) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        etProductName.setText(product?.namaProduk)
        etProductPrice.setText(product?.harga.toString())
        etProductDescription.setText(product?.deskripsi)
        Glide.with(this).load(product?.gambarUrl).into(ivProductImage)

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

        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val api = ApiClient.getClient().create(ApiService::class.java)
        val call = api.editProduct(product!!.idProduk!!, name, price.toInt(), description, imageBase64)

        call.enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProductActivity, "Produk berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductActivity, "Gagal mengubah produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteProduct() {
        val api = ApiClient.getClient().create(ApiService::class.java)
        val call = api.deleteProduct(product!!.idProduk!!)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProductActivity, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProductActivity, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@EditProductActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
