package com.dev.farmseller.ui.add_product

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dev.farmseller.ui.BaseFragment
import com.dev.farmseller.R
import com.dev.farmseller.databinding.FragmentAddProductBinding
import com.dev.farmseller.util.Constants.categoriesList
import com.dev.farmseller.util.Resource
import kotlinx.coroutines.launch
import java.time.LocalDate


class AddProductFragment : BaseFragment<FragmentAddProductBinding>(
    FragmentAddProductBinding::inflate){
    /* Define view model */
    private val viewModel: AddProductViewModel by viewModels()
    /* Initialised to zero as it is empty, until user adds images*/
    private var productPhotos: MutableList<Uri>? = null
    /* Selecting a category */
    private var selectedCategory = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeForObservers()
        viewModel.getSellerData()
        ui()
    }
    /*Learnt how to handle responses from: https://www.geeksforgeeks.org/how-to-handle-api-responses-success-error-in-android
    * */
    /*Listening objects to see what changes occur in the view model*/
    private fun subscribeForObservers() = lifecycleScope.launch {
        viewModel.uploadingStatus.observe(viewLifecycleOwner) { status ->
            /* There are 3 cases when we execute a network request
            * First: Loading -> executed the network request and we are waiting for the response from the server
            * Second: Error -> may occur for any reason
            * Third: Success -> received the data successfully */
            when(status) {
                is Resource.Loading -> {
                    /* Once we are on the loading state we will present a progress bar for the user */
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    /* Here we got the data successfully so we will hide the progress bar as we don't need it any more */
                    showSnack("Product has been added successfully")
                    findNavController().navigateUp()
                    binding.progressBar.visibility = View.INVISIBLE
                }
                is Resource.Error -> {
                    /* If there is an error so we will also hide the progress bar */
                    binding.progressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun categorySpinner() {
        /*Learnt the concept of the drop down menu but used category spinner instead from: https://www.youtube.com/watch?v=741l_fPKL3Y*/
        /* Array adapter holds list of data and needs a layout and the list of data -> Give it an xml layout and the list of categories */
        val categoryAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_item, categoriesList)
        binding.autoCompleteTextView.setAdapter(categoryAdapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            /* SelectedCategory variable is relayed the actual category the user chooses
            * categoriesList[position] means that the user chooses the first category in the list so selectedCategory = Fruits */
            selectedCategory = categoriesList[position]
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ui() {
        categorySpinner()
        /* Click listener on a button to launch an intent and send the user to the gallery */
       // Learnt about intents from: https://developer.android.com/training/basics/intents/result/
        binding.productPhotoBtn.setOnClickListener {
            pickMultipleMediaItems.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.submitBtn.setOnClickListener {
            addProduct()
        }
    }




   /*Learnt how to do this from here: https://developer.android.com/training/data-storage/shared/photopicker
   * Documentation shows only how to retrieve the photos, I coded it to handle a list of images */
    private val pickMultipleMediaItems = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { list ->

       /* This block executed after the user returns back from selecting the images*/
        /* Check if list is not empty */
        if (list.isNotEmpty()) {
            /* Not empty, so define productPhotos which is the variable instantiated on the top */
            productPhotos = list.toMutableList()
            lifecycleScope.launch {
                /* Loop to put all images on screen */
                productPhotos?.forEach { uri ->
                    val imageView = LayoutInflater.from(context)
                        .inflate(R.layout.product_image, binding.imagesLayout, false) as ImageView
                    binding.imagesLayout.addView(imageView)
                    /* Loading the photo to the image view */
                    Glide.with(requireContext()).load(uri).into(imageView)
                }
            }
        } else {
        }
    }

    /* Current user email that we already saved in the login fragment
    * Simple key-value pairs of data in local storage */
    private fun getUserEmail(): String? {
        return sharedPref.getString("userEmail", null)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addProduct() = lifecycleScope.launch {
        val productName = binding.productNameEt.text.toString()
        val categoryName = selectedCategory // Variable instantiated
        val productPrice = binding.priceEt.text.toString()
        val availableQuantity = binding.availableQuantityEt.text.toString().toInt()
        /*Learnt how to get the local date in the correct format from: https://developer.android.com/reference/kotlin/java/time/LocalDate*/
        val dateOfPublishing = LocalDate.now().dayOfYear.toLong()
        val expiry = LocalDate.now().plusDays(binding.expiryEt.text.toString().toLong()).dayOfYear.toLong()
        val description = binding.descriptionEt.text.toString()

        /* Giving the view model the list of photos that the user chooses */
        /* Uri is like an address - list of URIs with addresses */
        viewModel.productPhotos = productPhotos as ArrayList<Uri>
        getUserEmail()?.let {
            /* Product data sent to ViewModel -> ViewModel adds product to the database */
            viewModel.addProduct(
                sellerEmail = it,
                productName = productName,
                category = categoryName,
                price = productPrice,
                availableQuantity = availableQuantity,
                description = description,
                dateOfPublishing,
                expiry = expiry
            )
        }
    }


}