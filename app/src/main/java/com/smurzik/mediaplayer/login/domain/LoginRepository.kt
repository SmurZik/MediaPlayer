package com.smurzik.mediaplayer.login.domain


interface LoginRepository {

    suspend fun login(email: String, password: String): Result

    suspend fun register(email: String, username: String, password: String): Result

    suspend fun getToken(): String?

    suspend fun clearUser()

    suspend fun getUser(): Result

    suspend fun updateUser(email: String, username: String, password: String)
}