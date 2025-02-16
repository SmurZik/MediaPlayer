package com.smurzik.mediaplayer.cloud.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TrackService {

    @GET("chart")
    suspend fun chartTracks(): TracksCloud

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") id: Long): TrackContent

    @GET("search?")
    suspend fun searchTrack(@Query("q") query: String): TracksData
}