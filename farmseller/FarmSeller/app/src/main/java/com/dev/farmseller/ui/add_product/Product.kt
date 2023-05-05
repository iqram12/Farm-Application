package com.dev.farmseller.ui.add_product

data class Product(
    var sellerEmail: String = "",
    var sellerPhoneNumber: String = "",
    var sellerId: String = "",
    var sellerArea: String = "",
    var id: String = "",
    var productName: String = "",
    var category: String = "",
    var price: String = "",
    var availableQuantity: Int = 0,
    var description: String = "",
    var dateOfPublishing: Long = 0L,
    var expiry: Long = 0L,
    var images: ArrayList<String> = arrayListOf("")
)
