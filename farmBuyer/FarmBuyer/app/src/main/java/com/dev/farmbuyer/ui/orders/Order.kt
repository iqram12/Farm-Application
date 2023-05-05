package com.dev.farmbuyer.ui.orders

data class Order(
    val orderId: String = "",
    val total: String = "",
    val state: String = "Placed"
)
