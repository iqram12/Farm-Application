package com.dev.farmseller.ui.products

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.dev.farmseller.databinding.FragmentProductsBinding
import com.dev.farmseller.ui.BaseFragment
import com.dev.farmseller.ui.add_product.Product
import kotlinx.coroutines.launch

class ProductsFragment : BaseFragment<FragmentProductsBinding>(
    FragmentProductsBinding::inflate
), ProductsAdapter.OnItemClickListener {
    private val viewModel: ProductsViewModel by viewModels()
    private lateinit var adapter: ProductsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProductsRv()
        viewModel.getProducts()
        subscribeForObservers()
    }

    private fun subscribeForObservers() = lifecycleScope.launch {
        viewModel.products.observe(viewLifecycleOwner) { list ->
            adapter.differ.submitList(list)
        }
    }

    private fun setupProductsRv() {
        adapter = ProductsAdapter(this)
        binding.productsRv.adapter = adapter
        binding.productsRv.layoutManager = GridLayoutManager(context, 2)
    }

    override fun onItemClick(position: Int, view: View, product: Product) {
        viewModel.deleteProduct(product.id)
    }

}