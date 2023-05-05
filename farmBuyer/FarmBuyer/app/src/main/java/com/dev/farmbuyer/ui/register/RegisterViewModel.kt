package com.dev.farmbuyer.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    // Use of live data structure learnt from: https://www.kodeco.com/10391019-livedata-tutorial-for-android-deep-dive
    var registerStatus = MutableLiveData<Boolean>()

    /*Registration data into hashmap (Key Value Pair)*/
    fun registration(registerEmail: String,
                     registerUserName: String,
                     registerPhoneNumber: String,
                     registerPassword: String){
        val registrationForDatabase = hashMapOf(
            "email" to registerEmail,
            "userName" to registerUserName,
            "phoneNumber" to registerPhoneNumber
        )
        // Using firebase authentication to create user account and storing it in user data
        auth.createUserWithEmailAndPassword(registerEmail, registerPassword).addOnCompleteListener {
                task ->
            if (task.isSuccessful){
                db.collection("Buyers")
                    .document(getUserId())
                    .collection("UserData")
                    .document(getUserId())
                    .set(registrationForDatabase)
                // Sending the user to the login fragment once the account is set up
                registerStatus.postValue(true)
            }
        }
    }

    /*How to get user ID copied from: https://stackoverflow.com/questions/37566911/how-to-get-user-uid-from-firebase-on-android*/
    private fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid.toString()
    }
}