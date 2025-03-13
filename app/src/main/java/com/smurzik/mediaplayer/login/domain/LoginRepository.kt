package com.smurzik.mediaplayer.login.domain

interface LoginRepository {

    suspend fun login(email: String, password: String)

    suspend fun register(email: String, username: String, password: String)
}