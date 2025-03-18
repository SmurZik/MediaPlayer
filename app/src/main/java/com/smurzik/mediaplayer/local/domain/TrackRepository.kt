package com.smurzik.mediaplayer.local.domain

interface TrackRepository {

    suspend fun tracksList(): List<LocalTrackDomain>

    suspend fun searchTrack(query: String): List<LocalTrackDomain>

    suspend fun changeFavorite(track: LocalTrackDomain)
}