package com.dev.farmbuyer.ui.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.farmbuyer.ui.category.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ProductViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val uiEvent = MutableLiveData<String>()
    /* Decreasing the available quantity with the value of chosen quantity */
    fun updateProduct(product: CartProduct) = viewModelScope.launch {
        product.chosenQuantity?.let { quantity ->
            db.collection("Products")
                .document(product.id)
                   // Decreasing available quantity learnt from: https://stackoverflow.com/questions/55222414/increment-existing-value-in-firebase-firestore
                .update("availableQuantity", FieldValue.increment(-quantity.toLong())) /* Decreasing the available quantity with the value of chosen quantity */
                .addOnSuccessListener {
                    addProduct(product)
                }
        }
    }

    // Adding a product to the user's cart in Firebase Firestore database
    private fun addProduct(product: CartProduct)  = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("CartProducts")
            .document(product.id)
            .set(product)
            .addOnSuccessListener {
                uiEvent.postValue("Product has been added successfully to cart")
            }
    }

    /*How to get user ID copied from: https://stackoverflow.com/questions/37566911/how-to-get-user-uid-from-firebase-on-android*/
    private fun getUserId(): String {
        return auth.currentUser?.uid.toString()
    }
}