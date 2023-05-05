package com.dev.farmbuyer.ui.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    var currentCategory: String? = null

    var categoryData = MutableLiveData<List<Product>>()

      /*Fetch products for a specific catgeory from the database and
      * update the categoryData livedata object */
    fun getCategoryData() = viewModelScope.launch {
        db.collection("Products")
            .whereEqualTo("category", currentCategory)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val categoryProducts = snapshot.toObjects(Product::class.java)
                    categoryData.postValue(categoryProducts)
                }
            }
    }
}