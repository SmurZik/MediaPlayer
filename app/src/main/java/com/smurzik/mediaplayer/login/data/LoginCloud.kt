package com.smurzik.mediaplayer.login.data

data class RegistrationBody(
    val login: String,
    val password: String,
    val username: String
)

data class RegistrationResponse(
    val token: String
)

data class LoginBody(
    val login: String,
    val password: String
)

data class LoginResponse(
    val token: String
)