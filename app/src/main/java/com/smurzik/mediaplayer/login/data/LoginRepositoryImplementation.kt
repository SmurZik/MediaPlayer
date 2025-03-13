package com.smurzik.mediaplayer.login.data

import android.util.Log
import com.smurzik.mediaplayer.login.domain.LoginRepository
import com.smurzik.mediaplayer.login.presentation.UiState

class LoginRepositoryImplementation(
    private val loginService: LoginService
) : LoginRepository {
    override suspend fun login(
        email: String,
        password: String
    ) {
        val loginBody = LoginBody(
            email, password
        )
        val token = loginService.login(loginBody)
        Log.d("smurzLog", token.token)
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ) {
        val registrationBody = RegistrationBody(
            email, password, username
        )
        try {
            val token = loginService.register(registrationBody)
            Log.d("smurzLog", token.token)
        } catch (e: Exception) {
            Log.d("smurzLog", e.message.toString())
        }
    }
}