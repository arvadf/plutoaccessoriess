package com.example.plutoaccessories.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val productList: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Definisikan base URL untuk gambar di sini
    private val baseImageUrl = "http://192.168.1.6/pluto_api/uploads/"

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

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)

        fun bind(product: Product) {
            tvProductName.text = product.namaProduk
            
            val priceFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
            tvProductPrice.text = "Rp ${priceFormat.format(product.harga)}"
            
            val fullImageUrl = if (product.gambarUrl.isNullOrEmpty()) {
                null
            } else {
                baseImageUrl + product.gambarUrl
            }

            // Muat gambar dengan URL lengkap, dan gunakan placeholder generik dari sistem
            Glide.with(itemView.context)
                .load(fullImageUrl)
                .placeholder(R.drawable.ic_category_placeholder) // Placeholder abu-abu
                .error(R.drawable.ic_category_placeholder)       // Gambar error jika gagal muat
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProductImage)
        }
    }
}
