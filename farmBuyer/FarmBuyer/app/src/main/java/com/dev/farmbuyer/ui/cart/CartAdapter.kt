package com.dev.farmbuyer.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dev.farmbuyer.databinding.ItemCartPageBinding
import com.dev.farmbuyer.ui.product.CartProduct
import java.text.NumberFormat
import java.util.*

class CartAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // Interface behaviour learnt from: https://aayushpuranik.medium.com/recycler-view-using-kotlin-with-click-listener-46e7884eaf59
    // Click listener with recycler view learnt from: https://www.youtube.com/watch?v=dB9JOsVx-yY
    interface OnItemClickListener {
        /*View is null as we do not need to give the positive and negative view to the fragment
        - As we didn't any logic from the view - only needed the view for the delete button
        - Do not need the position either */
        fun onItemClick( view: View?, product: CartProduct)
    }

    class CartViewHolder(binding: ItemCartPageBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemName = binding.productName
        var itemPrice = binding.productPrice
        var itemImage = binding.productImage
        var itemQuantity = binding.productQuantity
        var category = binding.category
        var deleteItem = binding.deleteItem
        var positiveIcon = binding.positiveIcon
        var negativeIcon = binding.negativeIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            ItemCartPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /*  Concept learnt from: https://www.youtube.com/watch?v=wGDX9zjWQzE  - same concept
     * This method compares the old list with the new one and automatically adds the items *//*
      Checks if there is a new item in the cart page and updates accordingly*/
    private val differCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    // Structure from:https://www.youtube.com/watch?v=wGDX9zjWQzE but applied for my own adapter
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.itemName.text = product.productName
        val format = NumberFormat.getCurrencyInstance(Locale.UK)
        holder.itemPrice.text = format.format(product.price.toDouble()).toString()
        Glide.with(holder.itemImage)
            .load(product.images[0])
            .fitCenter()
            .into(holder.itemImage)
        holder.itemQuantity.text = product.chosenQuantity.toString()
        holder.category.text = product.category

        shouldShowDeleteButton(holder)

        holder.positiveIcon.setOnClickListener {
            val chosenQuantity = holder.itemQuantity.text.toString().toIntOrNull()
            if (chosenQuantity != null && product.overallQuantity != null) {
                if (chosenQuantity >= product.overallQuantity) {
                    Toast.makeText(holder.itemView.context, "Quantity can't be more than ${product.overallQuantity}", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            holder.itemQuantity.text = holder.itemQuantity.text.toString().toInt().plus(1).toString()
            product.chosenQuantity = holder.itemQuantity.text.toString().toInt()
            clickListener.onItemClick(null, product)
            shouldShowDeleteButton(holder)
        }

        holder.negativeIcon.setOnClickListener {
            if (holder.itemQuantity.text != "1"){
                holder.itemQuantity.text = holder.itemQuantity.text.toString().toInt().minus(1).toString()
                product.chosenQuantity = holder.itemQuantity.text.toString().toInt()
              /* Tells the cart fragment that the negative icon has been clicked on
                passing the product - the new product details to the fragment as the quantity has been changed
                - from the fragment it goes to the view model*/
                clickListener.onItemClick(null, product)
                shouldShowDeleteButton(holder)
            }
        }

        holder.deleteItem.setOnClickListener {
            clickListener.onItemClick(holder.deleteItem, product)
        }
    }

    private fun shouldShowDeleteButton(holder: CartViewHolder) {
        if (holder.itemQuantity.text.toString().toInt() == 1){
            holder.deleteItem.visibility = View.VISIBLE
        }
        else if (holder.itemQuantity.text.toString().toInt() > 1) {
            holder.deleteItem.visibility = View.GONE
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}