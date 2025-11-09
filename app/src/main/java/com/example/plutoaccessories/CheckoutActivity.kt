package com.example.plutoaccessories

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.models.ApiResponse
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.CartStore
import com.example.plutoaccessories.utils.SessionManager
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutActivity : AppCompatActivity() {

    private lateinit var etAddress: EditText
    private lateinit var rgPayment: RadioGroup
    private lateinit var rgShipping: RadioGroup
    private lateinit var btnPlaceOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        etAddress = findViewById(R.id.etAddress)
        rgPayment = findViewById(R.id.rgPayment)
        rgShipping = findViewById(R.id.rgShipping)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun placeOrder() {
        val address = etAddress.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Alamat tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPaymentId = rgPayment.checkedRadioButtonId
        val selectedShippingId = rgShipping.checkedRadioButtonId

        if (selectedPaymentId == -1 || selectedShippingId == -1) {
            Toast.makeText(this, "Pilih metode pembayaran dan pengiriman", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = findViewById<RadioButton>(selectedPaymentId).text.toString()
        val shippingMethod = findViewById<RadioButton>(selectedShippingId).text.toString()

        val sessionManager = SessionManager(this)
        val userId = sessionManager.userId
        val cartItems = CartStore.getItems()
        val totalHarga = cartItems.sumOf { it.harga * it.jumlah }
        val itemsJson = Gson().toJson(cartItems)

        val api = ApiClient.getClient().create(ApiService::class.java)
        val call = api.insertOrder(userId, totalHarga, itemsJson, address, paymentMethod, shippingMethod)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CheckoutActivity, "Pesanan berhasil dibuat", Toast.LENGTH_SHORT).show()
                    CartStore.clear()
                    finish()
                } else {
                    Toast.makeText(this@CheckoutActivity, "Gagal membuat pesanan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@CheckoutActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
