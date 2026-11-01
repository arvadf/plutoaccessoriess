package com.example.plutoaccessories.activities

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plutoaccessories.R
import com.example.plutoaccessories.databinding.ActivityCheckoutBinding
import com.example.plutoaccessories.models.Order
import com.example.plutoaccessories.models.OrderItem
import com.example.plutoaccessories.models.Product
import com.example.plutoaccessories.models.Wallet
import com.example.plutoaccessories.network.ApiClient
import com.example.plutoaccessories.network.ApiService
import com.example.plutoaccessories.utils.CartPrefs
import com.example.plutoaccessories.utils.CartStore
import com.example.plutoaccessories.utils.FormatHelper
import com.example.plutoaccessories.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private var currentWallet: Wallet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getClient().create(ApiService::class.java)
        sessionManager = SessionManager(this)

        fetchWalletBalance()

        binding.btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun fetchWalletBalance() {
        val userId = sessionManager.userId
        if (userId == -1) return

        apiService.getWallet(idUserFilter = "eq.$userId").enqueue(object : Callback<List<Wallet>> {
            override fun onResponse(call: Call<List<Wallet>>, response: Response<List<Wallet>>) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    currentWallet = response.body()!![0]
                    binding.tvWalletBalance.text = "(Saldo: ${FormatHelper.formatRupiah(currentWallet?.balance ?: 0.0)})"
                    binding.tvWalletBalance.visibility = View.VISIBLE
                }
            }
            override fun onFailure(call: Call<List<Wallet>>, t: Throwable) { /* Do nothing */ }
        })
    }

    private fun placeOrder() {
        val address = binding.etAddress.text.toString().trim()
        if (address.isEmpty()) {
            Toast.makeText(this, "Alamat tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPaymentId = binding.rgPayment.checkedRadioButtonId
        val selectedShippingId = binding.rgShipping.checkedRadioButtonId

        if (selectedPaymentId == -1 || selectedShippingId == -1) {
            Toast.makeText(this, "Pilih metode pembayaran dan pengiriman.", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentMethod = findViewById<RadioButton>(selectedPaymentId).text.toString()
        if (paymentMethod == "Bayar dengan Saldo Dompet") {
            handleWalletPayment()
        } else {
            createOrder(paymentMethod)
        }
    }

    private fun handleWalletPayment() {
        val totalPrice = CartStore.getItems().sumOf { it.harga * it.jumlah }
        val currentBalance = currentWallet?.balance ?: 0.0

        if (currentBalance < totalPrice) {
            Toast.makeText(this, "Saldo dompet tidak mencukupi.", Toast.LENGTH_SHORT).show()
            return
        }

        val newBalance = currentBalance - totalPrice
        apiService.updateWallet("eq.${sessionManager.userId}", mapOf("saldo" to newBalance)).enqueue(object: Callback<List<Wallet>> {
            override fun onResponse(p0: Call<List<Wallet>>, p1: Response<List<Wallet>>) {
                if(p1.isSuccessful){
                    createOrder("Saldo Dompet")
                } else {
                    Toast.makeText(this@CheckoutActivity, "Gagal memperbarui saldo dompet", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(p0: Call<List<Wallet>>, p1: Throwable) {
                Toast.makeText(this@CheckoutActivity, "Gagal memperbarui dompet", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createOrder(paymentMethod: String) {
        val shippingMethod = findViewById<RadioButton>(binding.rgShipping.checkedRadioButtonId).text.toString()
        val orderRequest = Order(
            userId = sessionManager.userId.toString(),
            totalPrice = CartStore.getItems().sumOf { it.harga * it.jumlah },
            address = binding.etAddress.text.toString().trim(),
            paymentMethod = paymentMethod,
            shippingMethod = shippingMethod,
            status = "Diproses"
        )

        apiService.insertOrder(orderRequest).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val newOrderId = response.body()?.firstOrNull()?.id
                    if (newOrderId != null) {
                        insertOrderItems(newOrderId)
                    } else {
                        Toast.makeText(this@CheckoutActivity, "Gagal mendapatkan ID pesanan baru", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@CheckoutActivity, "Gagal membuat pesanan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Toast.makeText(this@CheckoutActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun insertOrderItems(orderId: Long) {
        val orderItems = CartStore.getItems().map { cartItem ->
            OrderItem(
                orderId = orderId,
                productId = cartItem.idProduk.toLong(),
                quantity = cartItem.jumlah,
                priceAtOrder = cartItem.harga
            )
        }

        apiService.insertOrderItems(orderItems).enqueue(object : Callback<List<OrderItem>> {
            override fun onResponse(call: Call<List<OrderItem>>, response: Response<List<OrderItem>>) {
                if (response.isSuccessful) {
                    deductStock()
                    CartStore.clear()
                    CartPrefs.save(this@CheckoutActivity)
                    Toast.makeText(this@CheckoutActivity, "Pesanan berhasil dibuat", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CheckoutActivity, "Gagal menambahkan item pesanan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<OrderItem>>, t: Throwable) {
                Toast.makeText(this@CheckoutActivity, "Koneksi gagal saat menambahkan item: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deductStock() {
        CartStore.getItems().forEach { cartItem ->
            // Get the latest product details to ensure stock is accurate
            apiService.getProductDetail("eq.${cartItem.idProduk}").enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                        val product = response.body()!![0]
                        val newStock = (product.stok ?: 0) - cartItem.jumlah
                        
                        // Update the stock
                        apiService.editProduct("eq.${cartItem.idProduk}", mapOf("stok" to newStock)).enqueue(object : Callback<List<Product>> {
                            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) { /* Log success or do nothing */ }
                            override fun onFailure(call: Call<List<Product>>, t: Throwable) { /* Log failure */ }
                        })
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    // Handle failure to get product details
                }
            })
        }
    }
}
