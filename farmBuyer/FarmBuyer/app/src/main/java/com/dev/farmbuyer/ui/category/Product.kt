package com.dev.farmbuyer.ui.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val sellerEmail: String = "",
    val sellerPhoneNumber: String = "",
    val sellerId: String = "",
    val sellerArea: String = "",
    var id: String = "",
    val productName: String = "",
    val category: String = "",
    val price: String = "",
    val availableQuantity: Int = 0,
    val description: String = "",
    val dateOfPublishing: Long = 0,
    val expiry: Long = 0,
    var images: ArrayList<String> = arrayListOf("")
): Parcelable
