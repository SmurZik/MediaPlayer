package com.smurzik.mediaplayer.login.domain

sealed class Result {

    data object Success : Result()

    data class Failure(val message: String) : Result()

    data class SuccessUser(
        val login: String,
        val username: String,
        val password: String
    ) : Result()

    data object NotRegistered : Result()
}