package com.dev.farmseller.ui.products

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.farmseller.ui.add_product.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProductsViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /*Getting products of that specific seller*/
    val products = MutableLiveData<List<Product>>()
    fun getProducts() = viewModelScope.launch {
        db.collection("Products")
            .whereEqualTo("sellerId", getUserId()) /* Filter products according to the seller id */
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(Product::class.java)
                products.postValue(data)
            }
    }

    /*Deleting the product that was clicked on - after receiving the request from the Fragment*/
    fun deleteProduct(productId: String) = viewModelScope.launch {
        db.collection("Products")
            .document(productId)
            .delete().addOnSuccessListener {
                getProducts()
            }
    }

    /*How to get user ID copied from: https://stackoverflow.com/questions/37566911/how-to-get-user-uid-from-firebase-on-android*/
    private fun getUserId(): String {
        return auth.currentUser?.uid.toString()
    }
}