package com.smurzik.mediaplayer.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.smurzik.mediaplayer.R
import com.smurzik.mediaplayer.core.MediaPlayerApp

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginViewModel: LoginViewModel =
            (requireActivity().application as MediaPlayerApp).loginViewModel
        val exitButton = view.findViewById<Button>(R.id.exitButton)

        exitButton.setOnClickListener {
            loginViewModel.clearUser()
            (requireActivity().findNavController(R.id.containerView)).navigate(R.id.action_profileFragment_to_mainFragment)
        }
    }
}