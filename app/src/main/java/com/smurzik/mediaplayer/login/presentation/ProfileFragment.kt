package com.smurzik.mediaplayer.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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

        val profileViewModel: ProfileViewModel =
            (requireActivity().application as MediaPlayerApp).profileViewModel

        val exitButton = view.findViewById<Button>(R.id.exitButton)
        val loginEditText = view.findViewById<TextInputEditText>(R.id.emailEditText)
        val usernameEditText = view.findViewById<TextInputEditText>(R.id.usernameEditText)
        val usernameTextLayout = view.findViewById<TextInputLayout>(R.id.usernameTextLayout)
        val progressBar = view.findViewById<ProgressBar>(R.id.profileProgressBar)
        val editButton = view.findViewById<Button>(R.id.editButton)
        val errorTextView = view.findViewById<TextView>(R.id.errorTextView)

        loginViewModel.getUser()

        profileViewModel.changeEdit(false)

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().findNavController(R.id.containerView)
                    .navigate(R.id.action_profileFragment_to_mainFragment)
            }
        })

        exitButton.setOnClickListener {
            loginViewModel.clearUser()
            (requireActivity().findNavController(R.id.containerView)).navigate(R.id.action_profileFragment_to_mainFragment)
        }

        editButton.setOnClickListener {
            if (profileViewModel.profileState.value == ProfileUiState.Edit) {
                profileViewModel.updateUser(
                    loginEditText.text.toString(),
                    usernameEditText.text.toString()
                )
                profileViewModel.changeEdit(profileViewModel.profileState.value != ProfileUiState.Edit)

            } else {
                profileViewModel.changeEdit(profileViewModel.profileState.value != ProfileUiState.Edit)
            }
        }

        loginViewModel.userLiveData.observe(viewLifecycleOwner) {
            loginEditText.setText(it.login)
            usernameEditText.setText(it.username)
        }

        loginViewModel.progressUserLiveData.observe(viewLifecycleOwner) {
            progressBar.visibility = it
        }

        loginViewModel.registeredLiveData.observe(viewLifecycleOwner) {
            if (!it) requireActivity().findNavController(R.id.containerView)
                .navigate(R.id.action_profileFragment_to_loginFragment)
        }

        profileViewModel.profileState.observe(viewLifecycleOwner) { profileState ->
            profileState.apply(usernameTextLayout, editButton)
        }

        loginViewModel.error.observe(viewLifecycleOwner) {
            if (loginViewModel.error.value?.isNotEmpty() == true) {
                Toast.makeText(requireContext(), loginViewModel.error.value, Toast.LENGTH_SHORT)
                    .show()
                errorTextView.visibility = View.VISIBLE
            }
        }
    }
}