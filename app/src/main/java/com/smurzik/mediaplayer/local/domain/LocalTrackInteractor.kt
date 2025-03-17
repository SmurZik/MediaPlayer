package com.smurzik.mediaplayer.local.domain

import java.net.UnknownHostException

interface LocalTrackInteractor {

    suspend fun init(): LocalTrackResult

    suspend fun searchTrack(query: String): LocalTrackResult

    class Base(
        private val repository: TrackRepository
    ) : LocalTrackInteractor {
        override suspend fun init(): LocalTrackResult {
            try {
                val result = repository.tracksList()
                return LocalTrackResult.Success(result)
            } catch (e: UnknownHostException) {
                return LocalTrackResult.Failure(e.message ?: "")
            }
        }

        override suspend fun searchTrack(query: String): LocalTrackResult {
            try {
                val result = repository.searchTrack(query)
                return LocalTrackResult.Success(result)
            } catch (e: UnknownHostException) {
                return LocalTrackResult.Failure(e.message ?: "")
            }
        }
    }
}