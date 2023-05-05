package com.dev.farmseller.ui.home

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dev.farmseller.ui.BaseFragment
import com.dev.farmseller.R
import com.dev.farmseller.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {
    /* In this home fragment we just have 3 buttons
    * each button navigates us to a specific screen */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // How to use the Nav graph from:  https://developer.android.com/guide/navigation/navigation-getting-started
        /* nav_graph is a file that defines the navigation directions for all the fragments */

        /* This button will navigate us to orders fragment */
        binding.ordersBtn.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToOrdersFragment(), null)
        }
        /* This button will navigate us to add product fragment */
        binding.addProductBtn.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToAddProductFragment(), null)
        }
        binding.productsBtn.setOnClickListener {
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToProductsFragment(), null)
        }
        /* This button will sign the user out and navigate them to the login fragment */
        binding.signOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.homeFragment, true)
                .build()
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment(), navOptions)
        }
    }
}