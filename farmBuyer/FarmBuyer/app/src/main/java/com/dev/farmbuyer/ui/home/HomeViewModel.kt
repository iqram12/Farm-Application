package com.dev.farmbuyer.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.farmbuyer.ui.category.Product
import com.dev.farmbuyer.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val latestProducts = MutableLiveData<List<Product>>()


    @RequiresApi(Build.VERSION_CODES.O)
    fun getSomeProducts() = viewModelScope.launch {
        db.collection("Products")
            .get()
            .addOnSuccessListener { snapshots ->
                val products = snapshots.toObjects(Product::class.java)
                val listOfProducts = ArrayList<Product>()
                products.forEach { product ->
                    if (Constants.formatExpiryDate(product) <=0) {
                        deleteExpiredProduct(product)
                    } else {
                        listOfProducts.add(product)
                    }
                }
                latestProducts.postValue(listOfProducts)
            }
    }

    /* Database to Delete Expired products */
    private fun deleteExpiredProduct(product: Product) = viewModelScope.launch {
        db.collection("Products")
            .document(product.id)
            .delete()
    }
}