package com.dev.farmbuyer.ui.product

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.dev.farmbuyer.ui.BaseFragment
import com.dev.farmbuyer.R
import com.dev.farmbuyer.databinding.FragmentProductBinding
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class ProductFragment : BaseFragment<FragmentProductBinding>(
    FragmentProductBinding::inflate
) {
    private val viewModel: ProductViewModel by viewModels()
    /* Getting arguments that were passed to this fragment */
    private val args: ProductFragmentArgs by navArgs()
    private lateinit var sliderView : SliderView
    private lateinit var productSliderAdapter : ProductSliderAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sliderSetup()
        settingProductViews()
        quantityDropDownList()

        productOutOfStock()
        lifecycleScope.launch {
            viewModel.uiEvent.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.addToCartBtn.setOnClickListener {
            val chosenQuantity = binding.quantitySpinner.selectedItem.toString().toIntOrNull()
            // Using 'let' to access the nullable integer value chosenQuantity
            // making sure it is not null
            chosenQuantity?.let {
                val product = CartProduct(
                    sellerEmail = args.product.sellerEmail,
                    sellerPhoneNumber = args.product.sellerPhoneNumber,
                    sellerId = args.product.sellerId,
                    sellerArea = args.product.sellerArea,
                    id = args.product.id,
                    productName = args.product.productName,
                    category = args.product.category,
                    price = args.product.price,
                    chosenQuantity = it,
                    overallQuantity = args.product.availableQuantity,
                    description = args.product.description,
                    dateOfPublishing = args.product.dateOfPublishing,
                    expiry = args.product.expiry,
                    images = args.product.images
                )
                viewModel.updateProduct(product)
            }
        }
    }

    private fun productOutOfStock() {
        if (args.product.availableQuantity == 0) {
            showSnack("This product is out of stock!")
            binding.addToCartBtn.visibility = View.INVISIBLE
        }
    }

    /* Expiry logic -> To calculate the expiry of the product */
    private fun formatExpiryDate(): Int {
        val dateOfPublishing = args.product.dateOfPublishing
        // To get date from Stackoverflow: https://stackoverflow.com/questions/75699707/how-to-get-first-and-last-day-of-the-last-month-and-first-day-of-the-year.
        val now = LocalDate.now().dayOfYear.toLong()
        val differenceBetweenTodayAndPublishing = now - dateOfPublishing
        Log.e("difference", differenceBetweenTodayAndPublishing.toString())
        val expiry = args.product.expiry - differenceBetweenTodayAndPublishing
        val rightExpiry = expiry - now
        return rightExpiry.toInt()
    }

    // Setting views to display information about product
    private fun settingProductViews(){
        binding.productName.text = args.product.productName
        // Converting currency from:  https://stackoverflow.com/questions/45592109/how-can-i-convert-numbers-to-currency-format-in-android
        val format = NumberFormat.getCurrencyInstance(Locale.UK)
        /* Double means -> 0.0 or 1.5 */
        binding.productPrice.text = format.format(args.product.price.toDouble()).toString()
        if (formatExpiryDate() <= 0) {
            binding.expiry.text = "Expired"
        } else {
            binding.expiry.text = formatExpiryDate().toString()
        }
        binding.aboutContent.text = args.product.description
        /* If the seller didn't type any about content then we will hide it's view in the layout */
        if (binding.aboutContent.text == "null" || binding.aboutContent.text == ""){
            binding.aboutLayout.visibility = View.GONE
        }
        else {
            binding.aboutLayout.visibility = View.VISIBLE
        }
        binding.sellerEmail.text = args.product.sellerEmail
        /*binding.sellerPhone.text = args.product.sellerPhoneNumber*/
        binding.sellerArea.text = args.product.sellerArea
        binding.mapBtn.setOnClickListener {
            googleMaps(args.product.sellerArea)
        }
    }

    // Google maps intent copied from: https://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
    private fun googleMaps(area: String) {
        val gmIntent = Uri.parse("http://maps.google.com/maps?daddr=$area")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmIntent)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }
    /* Here I used a library for making list of images work as a slider that automatically slides between photos -
     documentation is in java, but adapted to koltin
    Documentation from: https://github.com/smarteist/Android-Image-Slider */
    private fun sliderSetup(){
        val imagesList = args.product.images.toCollection(ArrayList())
        sliderView = binding.slider
        productSliderAdapter = ProductSliderAdapter(imagesList)
        sliderView.setSliderAdapter(productSliderAdapter)
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    // Setting up dropdown list for quantity selection
    private fun quantityDropDownList() {
        // How to flatten list for available products from:  https://stackoverflow.com/questions/57356880/how-to-flatten-list-of-lists-in-kotlin
        // Drop down learnt from: https://www.youtube.com/watch?v=741l_fPKL3Y
        val list = listOf(1..args.product.availableQuantity).flatten()
        val quantityAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, list)
        binding.quantitySpinner.adapter = quantityAdapter
        binding.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
            // When nothing selected -> no action needed
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}