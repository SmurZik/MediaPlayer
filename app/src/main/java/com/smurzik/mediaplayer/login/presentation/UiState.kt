package com.smurzik.mediaplayer.login.presentation

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

abstract class UiState {

    abstract fun apply(
        emailTextView: TextInputEditText,
        usernameTextView: TextInputEditText,
        passwordTextView: TextInputEditText,
        repeatedPasswordTextView: TextInputEditText,
        registeredTextView: TextView,
        button: Button,
        usernameTextInputLayout: TextInputLayout,
        repeatedPasswordTextInputLayout: TextInputLayout
    )

    protected fun clear(
        emailTextView: TextInputEditText,
        usernameTextView: TextInputEditText,
        passwordTextView: TextInputEditText,
        repeatedPasswordTextView: TextInputEditText
    ) {
        emailTextView.text?.clear()
        usernameTextView.text?.clear()
        passwordTextView.text?.clear()
        repeatedPasswordTextView.text?.clear()
    }

    object Login : UiState() {
        override fun apply(
            emailTextView: TextInputEditText,
            usernameTextView: TextInputEditText,
            passwordTextView: TextInputEditText,
            repeatedPasswordTextView: TextInputEditText,
            registeredTextView: TextView,
            button: Button,
            usernameTextInputLayout: TextInputLayout,
            repeatedPasswordTextInputLayout: TextInputLayout
        ) {
            clear(emailTextView, usernameTextView, passwordTextView, repeatedPasswordTextView)
            usernameTextView.visibility = View.GONE
            repeatedPasswordTextView.visibility = View.GONE
            registeredTextView.text = "Не зарегистрированы?"
            button.text = "Войти"
            usernameTextInputLayout.visibility = View.GONE
            repeatedPasswordTextInputLayout.visibility = View.GONE
        }
    }

    object Register : UiState() {
        override fun apply(
            emailTextView: TextInputEditText,
            usernameTextView: TextInputEditText,
            passwordTextView: TextInputEditText,
            repeatedPasswordTextView: TextInputEditText,
            registeredTextView: TextView,
            button: Button,
            usernameTextInputLayout: TextInputLayout,
            repeatedPasswordTextInputLayout: TextInputLayout
        ) {
            clear(emailTextView, usernameTextView, passwordTextView, repeatedPasswordTextView)
            usernameTextView.visibility = View.VISIBLE
            repeatedPasswordTextView.visibility = View.VISIBLE
            registeredTextView.text = "Уже зарегистрированы?"
            button.text = "Зарегистрироваться"
            usernameTextInputLayout.visibility = View.VISIBLE
            repeatedPasswordTextInputLayout.visibility = View.VISIBLE
        }
    }
}