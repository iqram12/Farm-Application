package com.dev.farmseller.ui.order_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.farmseller.databinding.ItemOrderProductBinding
import com.dev.farmseller.ui.orders.OrderProduct
import java.text.NumberFormat
import java.util.*

class OrderProductsAdapter() : RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    // Holds the views
    class OrderProductsViewHolder (private var binding : ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root){
        var itemName = binding.productName
        var itemPrice = binding.productPrice
        var itemImage1 = binding.productImage
        var itemQuantity = binding.productQuantity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        return OrderProductsViewHolder(
            ItemOrderProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    /*  Concept learnt from: https://www.youtube.com/watch?v=wGDX9zjWQzE  - same concept
     * This method compares the old list with the new one and automatically adds the order *//*
      Checks if there is a new item in the orders page and updates accordingly*/
    private val differCallBack = object : DiffUtil.ItemCallback<OrderProduct>() {
        override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    // Stucture from:https://www.youtube.com/watch?v=wGDX9zjWQzE but applied for my own adapter
    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.itemName.text = product.productName
        val format = NumberFormat.getCurrencyInstance(Locale.UK)
        holder.itemPrice.text = format.format(product.price.toDouble()).toString()
        Glide.with(holder.itemImage1)
            .load(product.images[0])
            .into(holder.itemImage1)
        holder.itemQuantity.text = product.chosenQuantity.toString()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}