package com.dev.farmseller.ui.order_details

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.farmseller.ui.BaseFragment
import com.dev.farmseller.databinding.FragmentOrderDetailsBinding


class OrderDetailsFragment : BaseFragment<FragmentOrderDetailsBinding>(
    FragmentOrderDetailsBinding::inflate
) {
    // Retrieve the order details by navArgs - to save us from manually calling get...()
    private val args: OrderDetailsFragmentArgs by navArgs()
    private lateinit var recyclerView : RecyclerView
    private lateinit var myAdapter : OrderProductsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProductsRv()
    }

    // Binding with the XML
    private fun setupProductsRv() {
        recyclerView = binding.productsRv
        myAdapter = OrderProductsAdapter()
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        myAdapter.differ.submitList(args.orderProduct.toList())
    }
}