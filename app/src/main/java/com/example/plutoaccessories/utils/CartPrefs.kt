package com.example.plutoaccessories.utils

import android.content.Context
import com.example.plutoaccessories.models.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartPrefs {
	private const val PREF_NAME = "pluto_cart"
	private const val KEY_CART = "cart_items"

	fun save(context: Context) {
		val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
		val editor = prefs.edit()
		val json = Gson().toJson(CartStore.getItems())
		editor.putString(KEY_CART, json)
		editor.apply()
	}

	fun load(context: Context) {
		val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
		val json = prefs.getString(KEY_CART, null) ?: return
		val type = object : TypeToken<MutableList<CartItem>>() {}.type
		val items: MutableList<CartItem> = Gson().fromJson(json, type) ?: mutableListOf()
		// replace store contents
		val store = CartStore.getItems()
		store.clear()
		store.addAll(items)
	}
}
