package com.dev.farmbuyer.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.farmbuyer.databinding.ItemOrderBinding

/* // Interface behaviour learnt from: // https://aayushpuranik.medium.com/recycler-view-using-kotlin-with-click-listener-46e7884eaf59
* Click listener with recycler view learnt from: https://www.youtube.com/watch?v=dB9JOsVx-yY*/
class OrdersAdapter(private var clickListener: OnItemClickListener) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View, order: Order)
    }

    class OrdersViewHolder (private var binding : ItemOrderBinding) : RecyclerView.ViewHolder(binding.root){
        val orderId = binding.orderId
        val total = binding.total
        val state = binding.state
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /*  Concept learnt from: https://www.youtube.com/watch?v=wGDX9zjWQzE  - same concept
   * This method compares the old list with the new one and automatically adds the new order *//*
      Checks if there is a new item in the products page and updates accordingly*/
    private val differCallBack = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    // Structure from:https://www.youtube.com/watch?v=wGDX9zjWQzE but applied for my own adapter
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.orderId.text = order.orderId
        holder.total.text = order.total
        holder.state.text = order.state
        holder.root.setOnClickListener {
            clickListener.onItemClick(position, it, order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}