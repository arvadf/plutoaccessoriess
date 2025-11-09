package com.example.plutoaccessories.utils

import com.example.plutoaccessories.models.CartItem

object CartStore {
	private val items: MutableList<CartItem> = mutableListOf()

	fun getItems(): MutableList<CartItem> = items

	fun add(item: CartItem) {
		// If same product exists, increase qty
		val idx = items.indexOfFirst { it.idProduk == item.idProduk }
		if (idx >= 0) {
			val existing = items[idx]
			items[idx] = existing.copy(jumlah = existing.jumlah + item.jumlah)
		} else {
			items.add(item)
		}
	}

	fun remove(item: CartItem) {
		items.remove(item)
	}

	fun clear() {
		items.clear()
	}

	fun total(): Double = items.sumOf { it.harga * it.jumlah }
}
