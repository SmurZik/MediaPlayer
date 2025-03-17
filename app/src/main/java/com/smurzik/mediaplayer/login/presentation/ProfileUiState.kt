package com.smurzik.mediaplayer.login.presentation

import android.widget.Button
import com.google.android.material.textfield.TextInputLayout

interface ProfileUiState {

    fun apply(
        usernameTextLayout: TextInputLayout,
        passwordTextLayout: TextInputLayout,
        editButton: Button
    )

    object Edit : ProfileUiState {

        override fun apply(
            usernameTextLayout: TextInputLayout,
            passwordTextLayout: TextInputLayout,
            editButton: Button
        ) {
            usernameTextLayout.isEnabled = true
            passwordTextLayout.isEnabled = true
            editButton.text = "Сохранить"
        }
    }

    object Read : ProfileUiState {

        override fun apply(
            usernameTextLayout: TextInputLayout,
            passwordTextLayout: TextInputLayout,
            editButton: Button
        ) {
            usernameTextLayout.isEnabled = false
            passwordTextLayout.isEnabled = false
            editButton.text = "Редактировать"
        }
    }
}