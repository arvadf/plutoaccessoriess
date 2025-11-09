package com.example.plutoaccessories.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.CartItem
import com.example.plutoaccessories.utils.FormatHelper

class CartAdapter(
    private val cartList: MutableList<CartItem>,
    private val onRemoveClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // Definisikan base URL untuk gambar, samakan dengan ProductAdapter
    private val baseImageUrl = "http://192.168.1.6/pluto_api/uploads/"

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProductCart)
        val tvName: TextView = view.findViewById(R.id.tvNameCart)
        val tvPrice: TextView = view.findViewById(R.id.tvPriceCart)
        val tvQty: TextView = view.findViewById(R.id.tvQuantityCart)
        // Perbaiki tipe data dari ImageView menjadi View yang lebih umum untuk klik
        val btnRemove: View = view.findViewById(R.id.btnRemoveCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.tvName.text = item.nama
        holder.tvPrice.text = item.harga.let { FormatHelper.formatRupiah(it) }
        holder.tvQty.text = "Qty: ${item.jumlah}"

        // Logika memuat gambar yang sudah diperbaiki
        val fullImageUrl = if (item.imageUrl.isNullOrBlank()) {
            null
        } else {
            baseImageUrl + item.imageUrl
        }

        Glide.with(holder.itemView.context)
            .load(fullImageUrl)
            .placeholder(R.drawable.ic_category_placeholder) // Gunakan placeholder generik
            .error(R.drawable.ic_category_placeholder)       // Gunakan placeholder generik
            .into(holder.imgProduct)

        // Tombol hapus
        holder.btnRemove.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount(): Int = cartList.size
}
