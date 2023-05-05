package com.dev.farmseller.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dev.farmseller.R
import com.dev.farmseller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    /* Starting point is to connect this activity with the layout file which is activity_main.xml using view binding
    * so this binding variable will be used in this activity and all fragments */
    // To set up view binding taken from: https://medium.com/@abhineshchandra1234/view-binding-in-kotlin-android-bda2b35d3e29
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // How to navigate components from: https://dev.to/theplebdev/navigating-android-fragments-with-the-navigation-component-64b
        /* Will use on only one activity for the whole app
        * navhostfragment copied from: https://stackoverflow.com/questions/58703451/fragmentcontainerview-as-navhostfragment*/
        /* Fragments are a light part of the activity I will use a fragment for each page on both apps
        * And to start using fragments we will define the nav host fragment which is the container that will have all fragments */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}