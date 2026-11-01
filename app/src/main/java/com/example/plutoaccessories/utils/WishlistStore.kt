package com.example.plutoaccessories.utils

import com.example.plutoaccessories.models.Product

object WishlistStore {
    private val wishlist: MutableList<Product> = mutableListOf()

    fun getWishlist(): List<Product> = wishlist

    fun add(product: Product) {
        if (!wishlist.contains(product)) {
            wishlist.add(product)
        }
    }

    fun remove(product: Product) {
        wishlist.remove(product)
    }
}
