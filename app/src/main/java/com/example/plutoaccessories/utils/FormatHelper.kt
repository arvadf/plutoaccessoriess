package com.example.plutoaccessories.utils

import java.text.NumberFormat
import java.util.*

object FormatHelper {

    // Format harga ke format rupiah, contoh: Rp 75.000
    fun formatRupiah(amount: String): String {
        return try {
            val number = amount.replace("[^\\d]".toRegex(), "").toDouble()
            val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            format.maximumFractionDigits = 0
            format.format(number)
        } catch (e: Exception) {
            amount // jika gagal, kembalikan nilai aslinya
        }
    }

    // Format harga ke format rupiah (untuk angka langsung)
    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    // Capitalize huruf pertama dari setiap kata
    fun capitalizeWords(text: String): String {
        return text.split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
}
