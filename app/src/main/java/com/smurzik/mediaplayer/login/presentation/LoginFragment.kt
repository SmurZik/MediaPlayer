package com.smurzik.mediaplayer.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.smurzik.mediaplayer.R
import com.smurzik.mediaplayer.core.MediaPlayerApp

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val emailTextView = view.findViewById<TextInputEditText>(R.id.emailTextView)
        val usernameTextView = view.findViewById<TextInputEditText>(R.id.usernameTextView)
        val passwordTextView = view.findViewById<TextInputEditText>(R.id.passwordTextView)
        val repeatedPasswordTextView =
            view.findViewById<TextInputEditText>(R.id.repeatPasswordTextView)
        val loginButton = view.findViewById<Button>(R.id.loginButton)
        val registeredTextView = view.findViewById<TextView>(R.id.registered)
        val loginViewModel = (requireActivity().application as MediaPlayerApp).loginViewModel

        loginButton.setOnClickListener {
            if (loginViewModel.stateLiveData.value != UiState.Login)
                loginViewModel.register(
                    emailTextView.text.toString(),
                    usernameTextView.text.toString(),
                    passwordTextView.text.toString(),
                    repeatedPasswordTextView.text.toString()
                )
            else
                loginViewModel.login(
                    emailTextView.text.toString(),
                    passwordTextView.text.toString()
                )
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().findNavController(R.id.containerView)
                    .navigate(R.id.action_loginFragment_to_mainFragment)
            }
        })

        registeredTextView.setOnClickListener {
            if (loginViewModel.stateLiveData.value != UiState.Login) {
                loginViewModel.updateRegistered(true)
            } else {
                loginViewModel.updateRegistered(false)
            }
        }

        loginViewModel.stateLiveData.observe(viewLifecycleOwner) {
            it.apply(
                emailTextView,
                usernameTextView,
                passwordTextView,
                repeatedPasswordTextView,
                registeredTextView,
                loginButton
            )
        }

        loginViewModel.successLogin.observe(viewLifecycleOwner) { success ->
            if (success) requireActivity().findNavController(R.id.containerView)
                .navigate(R.id.action_loginFragment_to_mainFragment)
            else if (loginViewModel.error.value?.isNotEmpty() == true) {
                Toast.makeText(requireContext(), loginViewModel.error.value, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}