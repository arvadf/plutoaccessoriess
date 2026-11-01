package com.example.plutoaccessories.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var productList: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
        
        holder.itemView.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction { onProductClick(product) }
                        .start()
                }
                .start()
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateProducts(newProductList: List<Product>) {
        val diffCallback = ProductDiffCallback(this.productList, newProductList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.productList = newProductList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val vStockOverlay: View = itemView.findViewById(R.id.vStockOverlay)
        private val tvStockStatus: TextView = itemView.findViewById(R.id.tvStockStatus)

        fun bind(product: Product) {
            tvProductName.text = product.namaProduk
            
            val priceFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
            tvProductPrice.text = "Rp ${priceFormat.format(product.harga ?: 0.0)}"
            
            val imageUrl = product.gambarUrl
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
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProductImage)

            if (product.stok ?: 0 <= 0) {
                vStockOverlay.visibility = View.VISIBLE
                tvStockStatus.visibility = View.VISIBLE
            } else {
                vStockOverlay.visibility = View.GONE
                tvStockStatus.visibility = View.GONE
            }
        }
    }

    class ProductDiffCallback(
        private val oldList: List<Product>,
        private val newList: List<Product>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
