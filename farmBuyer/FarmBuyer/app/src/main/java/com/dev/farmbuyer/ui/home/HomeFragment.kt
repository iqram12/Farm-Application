package com.dev.farmbuyer.ui.home

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.farmbuyer.R
import com.dev.farmbuyer.ui.BaseFragment
import com.dev.farmbuyer.databinding.FragmentHomeBinding
import com.dev.farmbuyer.ui.category.CategoryAdapter
import com.dev.farmbuyer.ui.category.Product
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
), CategoryAdapter.OnItemClickListener {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: CategoryAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSomeProducts()
        setupLatestRv()
        subscribeForObservers()

        binding.logout.setOnClickListener {
            logout()
        }

        binding.fruitsLayout.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToCategoryFragment("Fruits"), null)
        }
        binding.vegetablesLayout.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToCategoryFragment("Vegetables"), null)
        }
        binding.wonkyvegLayout.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToCategoryFragment("Wonky Veg"), null)
        }
        binding.milkAndEggsLayout.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToCategoryFragment("Milk & Eggs"), null)
        }
    }

    private fun subscribeForObservers() = lifecycleScope.launch {
        viewModel.latestProducts.observe(viewLifecycleOwner) { products ->
            myAdapter.differ.submitList(products)
        }
    }

    // Here I used the category adapter as it is exactly the same logic it is just different product lists
    private fun setupLatestRv() {
        recyclerView = binding.latestRv
        myAdapter = CategoryAdapter(this)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    private fun logout() {
        viewModel.auth.signOut()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeFragment, true)
            .build()
        findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment(), navOptions)
    }

    override fun onItemClick( product: Product) {
        // Adding navigation argument
        // This data is coming from the adapter and we will pass them to the product fragment to introduce the details */
        findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToProductFragment(product), null)
    }
}