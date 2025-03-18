package com.smurzik.mediaplayer.favorite.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FavoriteService {

    @GET("favorite/fetch")
    suspend fun fetchFavorites(@Header("Bearer-Authorization") token: String): List<FavoriteCloud>

    @POST("favorite/change")
    suspend fun favoriteChange(
        @Header("Bearer-Authorization") token: String,
        @Body track: FavoriteCloud
    )
}