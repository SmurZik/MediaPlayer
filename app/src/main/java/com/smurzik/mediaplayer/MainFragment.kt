package com.smurzik.mediaplayer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smurzik.mediaplayer.core.MediaPlayerApp

class MainFragment : Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationView =
            view.findViewById<BottomNavigationView>(R.id.mainBottomNavigationView)
        val navController =
            (childFragmentManager.findFragmentById(R.id.mainContainerView) as NavHostFragment).navController
        val loginViewModel = (requireActivity().application as MediaPlayerApp).loginViewModel
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemSelectedListener { item ->
            return@setOnItemSelectedListener if (item.itemId == R.id.favoriteTrackFragment) {
//                requireActivity().findNavController(R.id.containerView)
//                    .navigate(R.id.action_mainFragment_to_loginFragment)
                false
            } else {
                NavigationUI.onNavDestinationSelected(item, navController)
                true
            }
        }
    }
}