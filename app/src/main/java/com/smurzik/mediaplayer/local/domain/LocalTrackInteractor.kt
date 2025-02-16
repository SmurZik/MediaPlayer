package com.smurzik.mediaplayer.local.domain

import android.util.Log


interface LocalTrackInteractor {

    suspend fun init(): LocalTrackResult

    suspend fun searchTrack(query: String): LocalTrackResult

    class Base(
        private val repository: TrackRepository
    ) : LocalTrackInteractor {
        override suspend fun init(): LocalTrackResult {
            return LocalTrackResult.Success(repository.tracksList())
        }

        override suspend fun searchTrack(query: String): LocalTrackResult {
            return LocalTrackResult.Success(repository.searchTrack(query))
        }
    }
}