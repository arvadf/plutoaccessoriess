package com.example.plutoaccessories.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plutoaccessories.R
import com.example.plutoaccessories.activities.OrderDetailActivity
import com.example.plutoaccessories.models.Order
import com.example.plutoaccessories.utils.FormatHelper

class OrderAdapter(
    private val orderList: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener { view ->
            val context = view.context
            val intent = Intent(context, OrderDetailActivity::class.java).apply {
                putExtra("ORDER_ID", order.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orderList.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)

        fun bind(order: Order) {
            val orderIdText = order.id?.let { "#$it" } ?: "-"
            tvOrderId.text = "Order ID: $orderIdText"
            tvOrderDate.text = order.createdAt?.substringBefore("T") ?: "-"
            tvOrderTotal.text = FormatHelper.formatRupiah(order.totalPrice ?: 0.0)

            val firstItemImage = order.items?.firstOrNull()?.product?.gambarUrl
            val imageSource = when {
                firstItemImage.isNullOrBlank() -> null
                firstItemImage.startsWith("http") || firstItemImage.startsWith("https") -> firstItemImage
                firstItemImage.startsWith("content://") -> null // Safely ignore invalid content URIs
                firstItemImage.startsWith("data:") -> firstItemImage
                else -> "data:image/jpeg;base64,$firstItemImage"
            }

            Glide.with(itemView.context)
                .load(imageSource)
                .placeholder(R.drawable.ic_category_placeholder)
                .error(R.drawable.ic_category_placeholder)
                .into(ivProductImage)
        }
    }
}
