package com.dev.farmseller.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.farmseller.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// Authentication learnt from: https://firebase.google.com/docs/auth/android/start

/* User authentication and login uisng Firebase Authentication directly */
class LoginViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()

    // Structure of how to use the live data and applied to the login state from: https://www.kodeco.com/10391019-livedata-tutorial-for-android-deep-dive
    /* A class to define the states to tell the user what happened in the network request
    * We can use this class with any kind of data, here for example we will get a firebase user type */
    val loginState = MutableLiveData<Resource<FirebaseUser>>()

    fun login(email: String, password: String) = viewModelScope.launch {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { task ->
            if (task.user != null) {
                loginState.postValue(Resource.Success(task.user))
            }
        }.addOnFailureListener {
            loginState.postValue(Resource.Error(it.message.toString(), null))
        }
    }
}