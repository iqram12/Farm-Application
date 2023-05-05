package com.dev.farmbuyer.ui.cart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.farmbuyer.ui.orders.Order
import com.dev.farmbuyer.ui.product.CartProduct
import com.dev.farmbuyer.util.Resource
import com.dev.farmbuyer.util.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CartViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var userData: UserData? = null
    var cartData = MutableLiveData<List<CartProduct>>()
    var total = MutableLiveData("0")
    val placeOrderStatus = MutableLiveData<Resource<Boolean>>()

    val stateMsg = MutableLiveData<String>()

    // To limit the number of orders
    fun orderLimit() = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("MyOrders")
            .get()
            .addOnSuccessListener { snapshot ->
                val orders = snapshot.toObjects(Order::class.java)
                var numberOfPlaced = 0
                orders.forEach {
                    if (it.state == "Placed") {
                        numberOfPlaced += 1
                    }
                }
                if (numberOfPlaced >= 3) {
                    /* Send UI message that you can't place another order now */
                    stateMsg.postValue("Sorry you can't place more than 3 orders!")
                } else {
                    placeOrder()
                }
            }
    }
    private fun placeOrder() = viewModelScope.launch {
        placeOrderStatus.postValue(Resource.Loading())
        // Hashmap use from:
        val orderDataForBuyer = HashMap<String, Any>()
        val orderDataForSeller = HashMap<String, Any>()
        /* We will use the date and time as the order id */
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
        val date = dateFormat.format(Date())

        /* This is an empty list */
        val listOfSellers = ArrayList<String>()
        /* Going in a loop for each product in the cart */
        /* Get every product seller to send for each seller of what will be his order products */
        cartData.value?.forEach { cartProduct ->
            /* If the seller is not already in the list -> we will add his ID to the list */
            if (!listOfSellers.contains(cartProduct.sellerId)) {
                listOfSellers.add(cartProduct.sellerId)
            }
            /* Adding this product to the order for buyer and seller */
            db.collection("Buyers")
                .document(getUserId())
                .collection("MyOrders")
                .document(date)
                .collection("Products")
                .document(cartProduct.id)
                .set(cartProduct)
            db.collection("Sellers")
                .document(cartProduct.sellerId)
                .collection("MyOrders")
                .document(date)
                .collection("Products")
                .document(cartProduct.id)
                .set(cartProduct)
        }
        /* Now the list of sellers we created before */
        /* We will go for a loop to each seller to sum their products */
        listOfSellers.forEach { seller ->
            /* Filtering the cart products to only this seller's products */
            val sellerProducts = cartData.value?.filter {
                it.sellerId == seller
            }

            var total = 0
            /* Adding each product price to the variable total */
            sellerProducts?.forEach {
                /* Multiplying the price to the quantity - ensuring that the quantity is not 0 */
                val subtotal = it.price.toInt() * it.chosenQuantity!!
                total += subtotal
            }
            /* This user data object is the buyer data */
            userData?.let { buyerData ->
                orderDataForSeller["buyerName"] = buyerData.userName
                orderDataForSeller["buyerPhone"] = buyerData.phoneNumber
            }
            orderDataForSeller["total"] = total.toString()
            orderDataForSeller["orderId"] = date
            orderDataForSeller["buyerId"] = getUserId()
            orderDataForSeller["state"] = "Placed"
            db.collection("Sellers")
                .document(seller)
                .collection("MyOrders")
                .document(date)
                .set(orderDataForSeller, SetOptions.merge())
        }

        /* Total cart for buyer */
        total.value?.let { totalValue ->
            orderDataForBuyer["total"] = totalValue
        }
        orderDataForBuyer["orderId"] = date
        orderDataForBuyer["state"] = "Placed"
        db.collection("Buyers")
            .document(getUserId())
            .collection("MyOrders")
            .document(date)
            .set(orderDataForBuyer)
        /* Clearing current after finishing */
        clearCart()
        placeOrderStatus.postValue(Resource.Success(null))
    }

    private fun clearCart() = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("CartProducts")
            .get()
            .addOnSuccessListener { snapshots ->
                for (product in snapshots) {
                    product.reference.delete()
                }
                getCartProducts()
            }
    }

    private fun setCartTotalOnDatabase() = viewModelScope.launch {
        val total = hashMapOf("total" to total.value.toString())
        db.collection("Buyers")
            .document(getUserId())
            .set(total, SetOptions.merge())

    }

    // Cart calculation
    fun updateProductsPrices() {
        total.value = "0"
        cartData.value?.forEach { product ->
            val price = product.price.toIntOrNull()
            val chosenQuantity = product.chosenQuantity
            if (price != null && chosenQuantity != null) {
                total.value = total.value?.toIntOrNull()?.plus(chosenQuantity * price).toString()
            }
        }
    }

    fun deleteItem(product: CartProduct) = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("CartProducts")
            .document(product.id)
            .delete()
            .addOnSuccessListener {
                getCartProducts()
            }
    }

    fun getCartProducts() = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("CartProducts")
            .get()
            .addOnSuccessListener { snapshot ->
                val cartProducts = snapshot.toObjects(CartProduct::class.java)
                cartData.postValue(cartProducts)
                setCartTotalOnDatabase()
            }
    }

    fun getBuyerData() = viewModelScope.launch {
        db.collection("Buyers")
            .document(getUserId())
            .collection("UserData")
            .document(getUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.e("document", document.data.toString())
                val user = document.toObject(UserData::class.java)
                userData = user
            }
    }

    /*How to get user ID copied from: https://stackoverflow.com/questions/37566911/how-to-get-user-uid-from-firebase-on-android*/
    private fun getUserId(): String {
        return auth.currentUser?.uid.toString()
    }
}