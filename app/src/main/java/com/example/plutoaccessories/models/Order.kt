package com.example.plutoaccessories.models

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id") val id: Int,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("total_harga") val totalHarga: Double
)
