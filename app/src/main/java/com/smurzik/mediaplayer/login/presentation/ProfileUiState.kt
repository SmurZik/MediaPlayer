package com.smurzik.mediaplayer.login.presentation

import android.widget.Button
import com.google.android.material.textfield.TextInputLayout

interface ProfileUiState {

    fun apply(
        usernameTextLayout: TextInputLayout,
        editButton: Button
    )

    object Edit : ProfileUiState {

        override fun apply(
            usernameTextLayout: TextInputLayout,
            editButton: Button
        ) {
            usernameTextLayout.isEnabled = true
            editButton.text = "Сохранить"
        }
    }

    object Read : ProfileUiState {

        override fun apply(
            usernameTextLayout: TextInputLayout,
            editButton: Button
        ) {
            usernameTextLayout.isEnabled = false
            editButton.text = "Редактировать"
        }
    }
}