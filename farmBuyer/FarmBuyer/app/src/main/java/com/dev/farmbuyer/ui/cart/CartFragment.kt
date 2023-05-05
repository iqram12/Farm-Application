package com.dev.farmbuyer.ui.cart

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.farmbuyer.ui.BaseFragment
import com.dev.farmbuyer.R
import com.dev.farmbuyer.databinding.FragmentCartBinding
import com.dev.farmbuyer.ui.product.CartProduct
import com.dev.farmbuyer.util.Resource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class CartFragment : BaseFragment<FragmentCartBinding>(
    FragmentCartBinding::inflate
), CartAdapter.OnItemClickListener {

    private val viewModel: CartViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: CartAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRecyclerView()
        subscribeForObservers()
        viewModel.getCartProducts()
        viewModel.getBuyerData()

        binding.checkoutBtn.setOnClickListener {
            viewModel.orderLimit()
        }
    }

    private fun subscribeForObservers() = lifecycleScope.launch {
        viewModel.cartData.observe(viewLifecycleOwner) { products ->
            viewModel.updateProductsPrices()
            /* If there are no products in the cart then we will show cart empty text view */
            // Own logic
            if (products.isEmpty()) {
                binding.emptyCartTv.visibility = View.VISIBLE
            } else {
                binding.emptyCartTv.visibility = View.GONE
            }
            myAdapter.differ.submitList(products)
        }
        // Get the total from the view model
        viewModel.total.observe(viewLifecycleOwner) { total ->
            total.toIntOrNull()?.let {
                // Currency logic from:
                val format = NumberFormat.getCurrencyInstance(Locale.UK)
                binding.total.text = format.format(it)
            }
        }
       /*Learnt how to handle responses from: https://www.geeksforgeeks.org/how-to-handle-api-responses-success-error-in-android*/
        viewModel.placeOrderStatus.observe(viewLifecycleOwner) { status ->
            when(status) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Order has been reserved successfully", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.INVISIBLE
                }
                else -> {}
            }
        }
        viewModel.stateMsg.observe(viewLifecycleOwner) {
            showSnack(it)
        }
    }


    private fun setupCartRecyclerView() {
        recyclerView = binding.cartRecycler
        myAdapter = CartAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = myAdapter
    }

    /*Learnt how to do the alert dialogue from: https://stackoverflow.com/questions/56098162/how-to-use-materialalertdialogbuilder-fine*/
    /* Showing alert dialog asking the user if he is sure to delete this item from the cart */
    private fun alertDialog(product: CartProduct) {
        MaterialAlertDialogBuilder(
            requireContext(),
            com.google.android.material.R.style.MaterialAlertDialog_Material3
        )
            .setMessage("Are you sure you want to delete this item?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton("Delete") { dialog, _ ->
                viewModel.deleteItem(product)
                dialog.dismiss()
            }
            .show()
    }

    // When we get the positive or negative icons responses with updated items and send to the view model
    override fun onItemClick( view: View?, product: CartProduct) {
        val modification = viewModel.cartData.value?.onEach {
            if (it.id == product.id) {
                it.chosenQuantity = product.chosenQuantity
            }
        }
        viewModel.cartData.postValue(modification)

        if (view?.id == R.id.deleteItem) {
            alertDialog(product)
        }
    }
}