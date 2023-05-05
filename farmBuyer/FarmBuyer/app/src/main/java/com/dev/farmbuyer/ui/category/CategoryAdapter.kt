package com.dev.farmbuyer.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.farmbuyer.databinding.ItemCategoryBinding
import java.text.NumberFormat
import java.util.*

// Interface behaviour learnt from: // https://aayushpuranik.medium.com/recycler-view-using-kotlin-with-click-listener-46e7884eaf59
class CategoryAdapter(private val mListener: OnItemClickListener) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick( product: Product)
    }

    class CategoryViewHolder(binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var itemTitle = binding.itemTitle
        var itemPrice = binding.itemPrice
        var itemImage = binding.itemImage
        var root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Concept of differcallback learnt from: https://www.youtube.com/watch?v=cppys4VYyvA
    // Check if the items in the two lists have changed
    private val differCallBack = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    // Structure from:https://www.youtube.com/watch?v=wGDX9zjWQzE but applied for my own adapter
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.itemTitle.text = product.productName
        val format = NumberFormat.getCurrencyInstance(Locale.UK)
        holder.itemPrice.text = format.format(product.price.toDouble()).toString()
        Glide.with(holder.itemImage)
            .load(product.images[0])
            .into(holder.itemImage)
        holder.root.setOnClickListener {
            mListener.onItemClick(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}