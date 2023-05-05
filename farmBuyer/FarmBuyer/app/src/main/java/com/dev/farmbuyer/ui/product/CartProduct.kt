package com.dev.farmbuyer.ui.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// For when the product is added to the cart - related to the Firebase DB

@Parcelize
data class CartProduct(
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val sellerId: String = "",
    val sellerArea: String = "",
    var id: String = "",
    val productName: String = "",
    val category: String = "",
    val price: String = "",
    var chosenQuantity: Int? = 0,
    val overallQuantity: Int? = 0,
    val description: String = "",
    val dateOfPublishing: Long = 0,
    val expiry: Long = 0,
    var images: ArrayList<String> = arrayListOf("")
): Parcelable
