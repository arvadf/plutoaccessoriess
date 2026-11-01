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

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProductCart)
        val tvName: TextView = view.findViewById(R.id.tvNameCart)
        val tvPrice: TextView = view.findViewById(R.id.tvPriceCart)
        val tvQty: TextView = view.findViewById(R.id.tvQuantityCart)
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

        // FIX: Logika untuk menangani URL gambar yang salah
        val imageUrl = item.imageUrl
        val finalImageUrl = when {
            imageUrl.isNullOrBlank() -> null
            imageUrl.startsWith("http") || imageUrl.startsWith("https") -> imageUrl
            imageUrl.startsWith("data:") -> imageUrl
            else -> "data:image/jpeg;base64,$imageUrl"
        }

        Glide.with(holder.itemView.context)
            .load(finalImageUrl)
            .placeholder(R.drawable.ic_category_placeholder)
            .error(R.drawable.ic_category_placeholder)
            .into(holder.imgProduct)

        holder.btnRemove.setOnClickListener {
            onRemoveClick(item)
        }
    }

    override fun getItemCount(): Int = cartList.size
}
