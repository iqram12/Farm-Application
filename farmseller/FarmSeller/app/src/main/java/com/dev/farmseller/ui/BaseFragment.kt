package com.dev.farmseller.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.viewbinding.ViewBinding
import com.dev.farmseller.R
import com.google.android.material.snackbar.Snackbar

// Basefragment for viewBinding copied from: https://www.youtube.com/watch?v=JZ1QJ_rU0lE
// Deriving other fragments from this class
abstract class BaseFragment<VB: ViewBinding> (
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment() {

    private var _binding: VB? = null

    val binding: VB
        get() = _binding as VB

    // For saving small data in key-value pairs
    lateinit var sharedPref: SharedPreferences

    // Safe navigate bug fix taken from:
    // https://nezspencer.medium.com/navigation-components-a-fix-for-navigation-action-cannot-be-found-in-the-current-destination-95b63e16152e
    fun NavController.safeNavigate(direction: NavDirections, navOptions: NavOptions?) {
        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction, navOptions)
        }
    }

    // Basefragment for viewBinding copied from (Part of the same tutorial): https://www.youtube.com/watch?v=JZ1QJ_rU0lE
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation).also {
            if (!binding.toString().contains("FragmentOrdersBinding")) {
                binding.root.animation = it.animation
            }
        }
        if (_binding == null) {
            throw IllegalArgumentException("Binding cannot be null")
        } else {
            return binding.root
        }
    }

    // Shared preference from: https://developer.android.com/training/data-storage/shared-preferences
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext()
            .getSharedPreferences("Settings", Context.MODE_PRIVATE)
    }
    /*Showsnack copied from: https://stackoverflow.com/questions/30978457/how-to-show-snackbar-when-activity-starts*/
    fun showSnack(text: String) {
        requireActivity()
        Snackbar.make(
            requireActivity().findViewById(R.id.rootLayout),
            text,
            Snackbar.LENGTH_LONG
        ).show()
    }
}