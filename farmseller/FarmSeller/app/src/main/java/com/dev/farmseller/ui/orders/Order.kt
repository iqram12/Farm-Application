package com.dev.farmseller.ui.orders

data class Order(
    val orderId: String = "",
    val buyerName: String = "",
    val buyerId: String = "",
    val buyerPhone: String = "",
    val total: String = "",
    var state: String = ""
)
