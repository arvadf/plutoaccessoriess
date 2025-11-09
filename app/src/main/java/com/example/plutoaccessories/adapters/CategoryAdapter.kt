package com.example.plutoaccessories.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.plutoaccessories.R
import com.example.plutoaccessories.models.Category
import com.google.android.material.card.MaterialCardView

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category, position == selectedPosition)
        
        holder.itemView.setOnClickListener {
            // Update selection with animation
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            
            // Scale animation
            it.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
            
            onCategoryClick(category)
        }
    }

    override fun getItemCount(): Int = categoryList.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryCard: MaterialCardView = itemView.findViewById(R.id.categoryCard)
        private val ivCategoryImage: ImageView = itemView.findViewById(R.id.ivCategoryImage)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: Category, isSelected: Boolean) {
            tvCategoryName.text = category.name
            
            // Update UI based on selection state
            if (isSelected) {
                categoryCard.strokeColor = ContextCompat.getColor(itemView.context, R.color.pluto_gold)
                categoryCard.strokeWidth = 4
                tvCategoryName.setTextColor(ContextCompat.getColor(itemView.context, R.color.pluto_gold))
            } else {
                categoryCard.strokeColor = ContextCompat.getColor(itemView.context, android.R.color.transparent)
                categoryCard.strokeWidth = 2
                tvCategoryName.setTextColor(ContextCompat.getColor(itemView.context, R.color.pluto_text_secondary))
            }
            
            Glide.with(itemView.context)
                .load(category.imageUrl)
                .placeholder(R.drawable.ic_category_placeholder)
                .error(R.drawable.ic_category_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivCategoryImage)
        }
    }
}
