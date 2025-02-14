package com.smurzik.mediaplayer.local.domain

interface LocalTrackRepository {

    suspend fun localTracksList(query: String): List<LocalTrackDomain>
}