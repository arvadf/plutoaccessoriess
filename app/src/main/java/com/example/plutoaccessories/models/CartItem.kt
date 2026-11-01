package com.example.plutoaccessories.models

import androidx.annotation.DrawableRes

data class CartItem(
    val idProduk: Int,
    val nama: String,
    val harga: Double,
    val jumlah: Int = 1,
    val imageUrl: String? = null,
    @DrawableRes val gambarRes: Int? = null
)
