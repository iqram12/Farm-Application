package com.dev.farmseller.ui.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.farmseller.databinding.ItemProductBinding
import com.dev.farmseller.ui.add_product.Product
import java.text.NumberFormat
import java.util.*

// Seller products page
class ProductsAdapter(private val mListener: OnItemClickListener) : RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(position: Int,view: View, product: Product)
    }

    class ProductsViewHolder(binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemTitle = binding.itemTitle
        val itemPrice = binding.itemPrice
        val itemImage = binding.itemImage
        val deleteItem = binding.deleteBtn
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /*  Concept learnt from: https://www.youtube.com/watch?v=wGDX9zjWQzE  - same concept
     * This method compares the old list with the new one and automatically adds the new product *//*
      Checks if there is a new item in the products page and updates accordingly*/
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
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.itemTitle.text = product.productName
        val format = NumberFormat.getCurrencyInstance(Locale.UK)
        holder.itemPrice.text = format.format(product.price.toDouble()).toString()
        Glide.with(holder.itemImage)
            .load(product.images[0])
            .into(holder.itemImage)

        holder.deleteItem.setOnClickListener {
            mListener.onItemClick(position, holder.root, product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}