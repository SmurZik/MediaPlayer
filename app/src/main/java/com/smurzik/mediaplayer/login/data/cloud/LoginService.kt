package com.smurzik.mediaplayer.login.data.cloud

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("register")
    suspend fun register(@Body registrationBody: RegistrationBody): RegistrationResponse

    @POST("login")
    suspend fun login(@Body loginBody: LoginBody): LoginResponse
}