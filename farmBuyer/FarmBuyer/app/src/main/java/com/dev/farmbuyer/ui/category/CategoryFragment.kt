package com.dev.farmbuyer.ui.category

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.farmbuyer.ui.BaseFragment
import com.dev.farmbuyer.databinding.FragmentCategoryBinding
import com.dev.farmbuyer.util.Constants.formatExpiryDate
import kotlinx.coroutines.launch

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(
    FragmentCategoryBinding::inflate
), CategoryAdapter.OnItemClickListener {
    private val viewModel: CategoryViewModel by viewModels()
    private val args: CategoryFragmentArgs by navArgs()

    private lateinit var recyclerView : RecyclerView
    private lateinit var myAdapter : CategoryAdapter
    // I added this list here to publicly access list of products any where inside this fragment
    private var myListOfProducts: List<Product>? = null
    @RequiresApi(Build.VERSION_CODES.O)

    // Initialize view model, RecylerView, UI elements and sets up observers
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentCategory = args.categoryName
        viewModel.getCategoryData()
        setupRecyclerView()
        ui()
        subscribeForObservers()
    }

    // Setting up UI elements with their listeners for category name text and sort button
    @RequiresApi(Build.VERSION_CODES.O)
    private fun ui() {
        binding.categoriesPage.text = args.categoryName
        binding.sortBtn.setOnClickListener {
            myListOfProducts?.let {
                myAdapter.differ.submitList(sort(it))
            }
        }
    }

    // Sorting the list of products accoridng to expiry
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sort(products: List<Product>): List<Product> {
        return products.sortedByDescending {
            formatExpiryDate(it)
        }
    }

    // LiveData and updates the adapter's list when new data is observed
    private fun subscribeForObservers() = lifecycleScope.launch {
        viewModel.categoryData.observe(viewLifecycleOwner) { listOfProducts ->
            myListOfProducts = listOfProducts
            myAdapter.differ.submitList(listOfProducts)
        }
    }

    // Initializing the adapter etc
    private fun setupRecyclerView() {
        recyclerView = binding.categoryRecycler
        myAdapter = CategoryAdapter(this)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)
    }

    // Clicked product (in recycler view) passed as an argument, navigates to the ProductFragment
    override fun onItemClick(product: Product) {
        findNavController().safeNavigate(CategoryFragmentDirections.actionCategoryFragmentToProductFragment(product), null)
    }
}