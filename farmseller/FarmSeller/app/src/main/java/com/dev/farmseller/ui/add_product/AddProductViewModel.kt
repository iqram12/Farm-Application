package com.dev.farmseller.ui.add_product

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.farmseller.data.UserData
import com.dev.farmseller.util.Constants.PRODUCTS
import com.dev.farmseller.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Authentication from:  https://firebase.google.com/docs/firestore/quickstart#kotlin+ktx
class AddProductViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance() /* Firebase Authentication */
    private val db = FirebaseFirestore.getInstance() /* Firebase Firestore (Our database) */
    private val storage = Firebase.storage /* Firebase storage (To save product photos) */

    /* Product photos list that will be taken from the fragment and saved on this list to be sent to the database */
    var productPhotos = ArrayList<Uri>()

    /* This live data is related to the product id -> holder class
    * Products are being added sequentially  */
    private val _productId = MutableLiveData<Int>()
    val productId : LiveData<Int> = _productId

    /* This live data related to getting user data from the database */
    private val userData = MutableLiveData<UserData?>()
    /* Uploading is a network process -> live data */
    val uploadingStatus = MutableLiveData<Resource<Boolean>>()

    /* Current Seller data in order to add the product to the correct seller  */
    fun getSellerData() = viewModelScope.launch {
        db.collection("Sellers")
            .document(getUserId())
            .collection("UserData")
            .document(getUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e("document", document.data.toString())
                val user = document.toObject(UserData::class.java)
                /* Sending user data to live data, after receiving it*/
                userData.postValue(user)
            }
    }

    private fun addProductPhoto(photo: Uri, category: String) = viewModelScope.launch {
        /* Send to the live data a loading state */
        uploadingStatus.postValue(Resource.Loading())
        storage.reference
            .child("Products/$category/${_productId.value}/${photo.lastPathSegment}")
            .putFile(photo) /* Putting the photo on the database */
            .addOnSuccessListener {
                /* Put the link of the photo with the product data for uploading to buyer app */
                storage.reference.
                child("Products/$category/${_productId.value}/${photo.lastPathSegment}")
                    .downloadUrl
                    .addOnSuccessListener {
                        Log.e("images", it.toString())
                        /* We got the link so now we will add it to the product data */
                        db.collection("Products")
                            .document(productId.value.toString())
                            .update(
                                "images",
                                FieldValue.arrayUnion(it.toString()),
                                "images",
                                FieldValue.arrayRemove("")
                            )
                        /* Ui success message */
                        uploadingStatus.postValue(Resource.Success(null))
                    }.addOnFailureListener {
                        uploadingStatus.postValue(Resource.Error("Failed"))
                    }
            }.addOnFailureListener {
                uploadingStatus.postValue(Resource.Error("Failed"))
            }
    }

    // Finding the largest value ID among all the documents in the product collection.
    private var biggestId = 0
    private fun getBiggestIdInTheDb() = viewModelScope.launch {
        val listOfIds = ArrayList<Int>()
        db.collection("Products")
            .addSnapshotListener { value, error ->
                value?.documents?.forEach { document ->
                    listOfIds.add(document.id.toInt())
                }
                biggestId = listOfIds.maxOrNull() ?: 0
            }
    }

    fun addProduct(
        sellerEmail: String,
        productName : String,
        category : String,
        price : String,
        availableQuantity : Int,
        description : String,
        dateOfPublishing: Long,
        expiry: Long
    ) = viewModelScope.launch {
        getBiggestIdInTheDb()
        userData.value?.let {
            val product = Product(
                sellerEmail = sellerEmail,
                sellerPhoneNumber = it.phoneNumber, /* it here refers to seller phone number */
                sellerId = getUserId(), /* Current seller id */
                sellerArea = it.area,
                id = _productId.value.toString(), /* Current product id */
                productName = productName,
                category = category,
                price = price,
                description = description,
                availableQuantity = availableQuantity,
                dateOfPublishing = dateOfPublishing,
                expiry = expiry
            )

            db.collection("Products")
                .orderBy("id", Query.Direction.DESCENDING).limit(1).get() /* Making the query descending to get largest product id */
                .addOnCompleteListener {
                    /* View model scope is related to coroutines which is used for asynchronous tasks */
                    viewModelScope.launch {
                        /* To ensure that the request is successful and not from the cache
                       * as in firebase if there is no internet connection, firebase let you do changes offline
                       * then it will be auto directed to the database when the user became online */
                        if (it.isSuccessful && it.result?.metadata?.isFromCache == false) {
                            /* If the collection is empty that means that this is the first ever product to be added
                            * so we will add it with id = 1 */
                            if (it.result.isEmpty) {
                                Log.e("empty", "empty")
                                _productId.value = 1
                                product.id = _productId.value.toString()
                                db.collection(PRODUCTS)
                                    .document(_productId.value.toString())
                                    .set(product)
                                    .await()
                                /* add each photo to the product data */
                                productPhotos.forEach { photo ->
                                    addProductPhoto(photo, category)
                                }
                            } else {
                                /* There are old products already -> get largest product number */
                                it.result
                                for (every in it.result!!) {
                                    Log.e("every", every.toString())
                                    /* add 1 to the last available id */
                                    _productId.value = biggestId +1
                                    product.id = _productId.value.toString()
                                    db.collection(PRODUCTS)
                                        .document(productId.value.toString())
                                        .set(product)
                                        .await()
                                    productPhotos.forEach { photo ->
                                        addProductPhoto(photo, category)
                                    }
                                }
                            }

                        }
                    }
                }.addOnCanceledListener {
                    Log.e("cancel", "")
                }
        }


    }

    /* Every user in firebase has a unique id which we are using to save data related to this user */
    private fun getUserId(): String {
        return auth.currentUser?.uid.toString()
    }
}