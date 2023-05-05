package com.dev.farmbuyer.ui.orders

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Passing whole data class to the navigation argument rather than individually
@Parcelize
data class OrderProduct(
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val sellerId: String = "",
    val sellerArea: String= "",
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
