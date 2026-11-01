package com.example.plutoaccessories.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Transaction
import com.example.plutoaccessories.utils.FormatHelper

class TransactionAdapter(
    private val transactionList: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = transactionList.size

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDesc: TextView = itemView.findViewById(R.id.tvTransactionDesc)
        private val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvTransactionStatus)

        fun bind(transaction: Transaction) {
            tvDesc.text = transaction.description
            tvDate.text = transaction.createdAt?.substringBefore("T") ?: "-"

            if (transaction.type.equals("Top-Up", ignoreCase = true)) {
                tvAmount.text = "+ ${FormatHelper.formatRupiah(transaction.amount)}"
                tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.pluto_success))
            } else {
                tvAmount.text = "- ${FormatHelper.formatRupiah(transaction.amount)}"
                tvAmount.setTextColor(ContextCompat.getColor(itemView.context, R.color.pluto_danger))
            }

            tvStatus.text = "Status: ${transaction.status}"
            when (transaction.status) {
                "Diproses" -> tvStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
                "Berhasil" -> tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.pluto_success))
                "Gagal" -> tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.pluto_danger))
            }
        }
    }
}
