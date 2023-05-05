package com.dev.farmbuyer.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class OrdersViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val ordersStatus = MutableLiveData<List<Order>>()
    val orderProducts = MutableLiveData<List<OrderProduct>>()

    /*Live data to get the product orders*/
    fun getOrderProducts(order: Order) = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("MyOrders")
            .document(order.orderId)
            .collection("Products")
            .get()
            .addOnSuccessListener { snapshots ->
                val products = snapshots.toObjects(OrderProduct::class.java)
                orderProducts.postValue(products)
            }
    }

    fun myOrders() = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("MyOrders")
            .orderBy("orderId")
            .get()
            .addOnSuccessListener { snapshots ->
                val orders = snapshots.toObjects(Order::class.java)
                ordersStatus.postValue(orders)
            }
    }

    /*How to get user ID copied from: https://stackoverflow.com/questions/37566911/how-to-get-user-uid-from-firebase-on-android*/
    private fun getUserId(): String {
        return auth.currentUser?.uid.toString()
    }
}