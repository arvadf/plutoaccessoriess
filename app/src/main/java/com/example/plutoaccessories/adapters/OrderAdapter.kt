package com.example.plutoaccessories.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.activities.OrderDetailActivity
import com.example.plutoaccessories.models.Order
import com.example.plutoaccessories.utils.FormatHelper
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val orderList: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // Format untuk parsing tanggal dari server (Y-m-d H:i:s)
    private val serverDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    // Format untuk menampilkan tanggal ke user (d MMMM yyyy)
    private val displayDateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)

        // Tambahkan OnClickListener untuk setiap item
        holder.itemView.setOnClickListener { view ->
            val context = view.context
            val intent = Intent(context, OrderDetailActivity::class.java).apply {
                // Kirim ID pesanan ke OrderDetailActivity
                putExtra("ORDER_ID", order.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orderList.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)

        fun bind(order: Order) {
            tvOrderId.text = "Order ID: #${order.id}"
            tvOrderTotal.text = "Total: ${FormatHelper.formatRupiah(order.totalHarga)}"

            // Format tanggal agar lebih mudah dibaca
            try {
                val date = serverDateFormat.parse(order.tanggal ?: "")
                tvOrderDate.text = if (date != null) displayDateFormat.format(date) else "Tanggal tidak valid"
            } catch (e: Exception) {
                tvOrderDate.text = order.tanggal // Tampilkan tanggal asli jika format gagal
            }
        }
    }
}
