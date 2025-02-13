package com.smurzik.mediaplayer.local.domain

interface LocalTrackRepository {

    suspend fun localTracksList(): List<LocalTrackDomain>
}