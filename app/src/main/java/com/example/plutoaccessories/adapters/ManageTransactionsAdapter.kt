package com.example.plutoaccessories.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Transaction

class ManageTransactionsAdapter(
    private var transactions: List<Transaction>,
    private val onAccept: (Transaction) -> Unit,
    private val onReject: (Transaction) -> Unit
) : RecyclerView.Adapter<ManageTransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_manage_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount(): Int = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvInfo: TextView = itemView.findViewById(R.id.tvTransactionInfo)
        private val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvTransactionStatus)
        private val llActions: LinearLayout = itemView.findViewById(R.id.llActionButtons)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnReject: Button = itemView.findViewById(R.id.btnReject)

        fun bind(transaction: Transaction) {
            val info = "${transaction.type} oleh ${transaction.user?.nama ?: "User ID: ${transaction.userId}"} - Rp ${transaction.amount}"
            tvInfo.text = info
            tvDate.text = transaction.createdAt
            tvStatus.text = "Status: ${transaction.status}"

            if (transaction.status == "Diproses") {
                llActions.visibility = View.VISIBLE
                btnAccept.setOnClickListener { onAccept(transaction) }
                btnReject.setOnClickListener { onReject(transaction) }
            } else {
                llActions.visibility = View.GONE
            }
        }
    }
}
