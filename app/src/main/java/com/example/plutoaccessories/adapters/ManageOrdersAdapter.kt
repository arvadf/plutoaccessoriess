package com.example.plutoaccessories.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Order

class ManageOrdersAdapter(
    private var orders: List<Order>,
    private val onAccept: (Order) -> Unit,
    private val onReject: (Order) -> Unit
) : RecyclerView.Adapter<ManageOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manage_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        private val llActions: LinearLayout = itemView.findViewById(R.id.llActionButtons)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnReject: Button = itemView.findViewById(R.id.btnReject)

        fun bind(order: Order) {
            val firstItem = order.items?.firstOrNull()
            tvProductName.text = firstItem?.product?.namaProduk ?: "Produk tidak ditemukan"
            tvUserName.text = "oleh ${order.user?.nama ?: "User tidak ditemukan"}"
            tvOrderStatus.text = "Status: ${order.status}"

            val imageUrl = firstItem?.product?.gambarUrl
            val imageSource = when {
                imageUrl.isNullOrBlank() -> null
                imageUrl.startsWith("http") || imageUrl.startsWith("https") -> imageUrl
                imageUrl.startsWith("content://") -> null
                imageUrl.startsWith("data:") -> imageUrl
                else -> "data:image/jpeg;base64,$imageUrl"
            }

            Glide.with(itemView.context)
                .load(imageSource)
                .placeholder(R.drawable.ic_category_placeholder)
                .error(R.drawable.ic_category_placeholder)
                .into(ivProductImage)

            if (order.status == "Diproses") {
                llActions.visibility = View.VISIBLE
                btnAccept.setOnClickListener { onAccept(order) }
                btnReject.setOnClickListener { onReject(order) }
            } else {
                llActions.visibility = View.GONE
            }
        }
    }
}
