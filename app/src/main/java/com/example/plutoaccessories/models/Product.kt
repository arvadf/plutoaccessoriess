package com.example.plutoaccessories.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("id_produk") val id: Long? = null,
    @SerializedName("nama_produk") val namaProduk: String?,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("harga") val harga: Double?,
    @SerializedName("stok") val stok: Int?,
    @SerializedName("gambar") val gambarUrl: String?,
    @SerializedName("bahan") val bahan: String?,
    @SerializedName("merk") val merk: String?,
    @SerializedName("kategori") val kategori: String?,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("warna") val warna: String? = null,
    @SerializedName("berat") val berat: Double? = null
) : Parcelable
