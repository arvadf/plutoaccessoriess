package com.example.plutoaccessories.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    @SerializedName("id_order") val id: Long? = null,
    @SerializedName("id_user") val userId: String? = null,
    @SerializedName("tanggal_order") val createdAt: String? = null,
    @SerializedName("total_harga") val totalPrice: Double? = null,
    @SerializedName("alamat") val address: String? = null,
    @SerializedName("metode_pembayaran") val paymentMethod: String? = null,
    @SerializedName("opsi_pengiriman") val shippingMethod: String? = null,
    @SerializedName("status_pesanan") val status: String? = null,

    @SerializedName("users") @Expose(serialize = false) val user: UserModel? = null,
    @SerializedName("order_items") @Expose(serialize = false) val items: List<OrderItem>? = null
) : Parcelable

@Parcelize
data class OrderItem(
    @SerializedName("id_item") val id: Long? = null,
    @SerializedName("id_order") val orderId: Long? = null,
    @SerializedName("id_produk") val productId: Long,
    @SerializedName("jumlah") val quantity: Int,
    @SerializedName("harga_saat_order") val priceAtOrder: Double? = null,
    @SerializedName("products") @Expose(serialize = false) val product: Product? = null
) : Parcelable
