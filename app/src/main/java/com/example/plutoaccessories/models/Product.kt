package com.example.plutoaccessories.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
        @SerializedName("id_produk") val idProduk: Int?,
        @SerializedName("nama_produk") val namaProduk: String,
        @SerializedName("deskripsi") val deskripsi: String?,
        @SerializedName("harga") val harga: Double?,
        @SerializedName("stok") val stok: Int?,
        @SerializedName("gambar") val gambarUrl: String?,
        @SerializedName("bahan") val bahan: String?,
        @SerializedName("merk") val merk: String?
) : Parcelable
