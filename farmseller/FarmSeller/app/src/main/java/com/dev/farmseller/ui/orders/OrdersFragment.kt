package com.dev.farmseller.ui.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.farmseller.ui.BaseFragment
import com.dev.farmseller.databinding.FragmentOrdersBinding
import kotlinx.coroutines.launch

class OrdersFragment : BaseFragment<FragmentOrdersBinding>(
    FragmentOrdersBinding::inflate
), OrdersAdapter.OnItemClickListener, OrdersAdapter.OnCheckListener {
    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var recyclerView : RecyclerView
    private lateinit var myAdapter : OrdersAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.myOrders()
        subscribeForObservers()
        setupOrdersRv()
    }

    private fun subscribeForObservers() = lifecycleScope.launch {
        viewModel.ordersStatus.observe(viewLifecycleOwner) { orders ->
            /* Tell the adapter with the new data */
            myAdapter.differ.submitList(orders)
        }
        viewModel.orderProducts.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                /* On the order details fragment on the nav graph
                * we can see Arguments that we are passing
                * a list of orderProduct model which has the products of this order */

                // Learnt how to pass the arguments to the navigation from: https://www.youtube.com/watch?v=vx1-V3HH0IU&t=2s
                // I am passing the product in the argument instead.
                findNavController().safeNavigate(OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(
                    products.toTypedArray()
                ), null)
            }
            /* Make the list empty and leave this fragment */
            viewModel.orderProducts.postValue(emptyList())
        }
    }

    // Recycler Views learnt from: https://www.youtube.com/watch?v=UbP8E6I91NA
    private fun setupOrdersRv() {
        recyclerView = binding.ordersRecycler
        myAdapter = OrdersAdapter(this, this)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onItemClick(position: Int, view: View, order: Order) {
        /* When the user clicks on an order we will navigate the user to the order details fragment */
        viewModel.getOrderProducts(order)
    }

    override fun onCheckClick(position: Int, order: Order) {
        viewModel.updateOrderState(order)
    }
}