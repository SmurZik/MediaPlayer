package com.smurzik.mediaplayer.login.data.cloud

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginService {

    @POST("register")
    suspend fun register(@Body registrationBody: RegistrationBody): RegistrationResponse

    @POST("login")
    suspend fun login(@Body loginBody: LoginBody): LoginResponse

    @GET("user/fetch")
    suspend fun fetchUser(@Header("Bearer-Authorization") token: String): UserResponse

    @POST("user/update")
    suspend fun updateUser(@Header("Bearer-Authorization") token: String, @Body user: UserReceive)
}