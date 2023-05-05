package com.dev.farmbuyer.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dev.farmbuyer.ui.category.Product
import java.time.LocalDate

object Constants {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatExpiryDate(product: Product): Int {
        val dateOfPublishing = product.dateOfPublishing
        val now = LocalDate.now().dayOfYear.toLong()
        val differenceBetweenTodayAndPublishing = now - dateOfPublishing
        Log.e("difference", differenceBetweenTodayAndPublishing.toString())
        val expiry = product.expiry - differenceBetweenTodayAndPublishing
        val rightExpiry = expiry - now
        return rightExpiry.toInt()
    }
}