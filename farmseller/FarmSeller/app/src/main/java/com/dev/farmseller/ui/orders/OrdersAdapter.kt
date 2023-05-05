package com.dev.farmseller.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.farmseller.R
import com.dev.farmseller.databinding.ItemOrderBinding

/* Learnt about recycler Views from: https://www.youtube.com/watch?v=UbP8E6I91NA*/
class OrdersAdapter(private var clickListener: OnItemClickListener, private var checkListener: OnCheckListener) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View, order: Order)
    }

    interface OnCheckListener {
        fun onCheckClick(position: Int, order: Order)
    }

    class OrdersViewHolder (private var binding : ItemOrderBinding) : RecyclerView.ViewHolder(binding.root){
        val orderId = binding.orderId
        val buyerName = binding.buyerName
        val buyerPhone = binding.buyerPhone
        val total = binding.total
        val actionsGroup = binding.actionsGroup
        val placedChip = binding.placedChip
        val pendingCollectionChip = binding.pendingCollectionChip
        val collectedChip = binding.collectedChip
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    /*  Concept learnt from: https://www.youtube.com/watch?v=wGDX9zjWQzE  - same concept
     * This method compares the old list with the new one and automatically adds the new product *//*
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

    /* Learnt basic structure of how to use chips from: https://m2.material.io/components/chips/android#using-chips  logic  is my own*/
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.orderId.text = order.orderId
        holder.buyerName.text = order.buyerName
        holder.buyerPhone.text = order.buyerPhone
        holder.total.text = order.total
        order.state.apply {
            when(this) {
                "Placed" -> { holder.actionsGroup.check(R.id.placed_chip) }
                "Out For Delivery" -> { holder.actionsGroup.check(R.id.pending_collection_chip) }
                "Delivered" -> { holder.actionsGroup.check(R.id.collected_chip) }
            }
        }
        holder.placedChip.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked) {
                order.state = "Placed"
                checkListener.onCheckClick(position, order)
            }
        }
        holder.pendingCollectionChip.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked) {
                order.state = "Pending Collection"
                checkListener.onCheckClick(position, order)
            }
        }
        holder.collectedChip.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked) {
                order.state = "Collected"
                checkListener.onCheckClick(position, order)
            }
        }
        // Root layout
        holder.root.setOnClickListener {
            clickListener.onItemClick(position, it, order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}