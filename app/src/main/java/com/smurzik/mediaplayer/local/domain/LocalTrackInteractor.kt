package com.smurzik.mediaplayer.local.domain

interface LocalTrackInteractor {

    suspend fun init(): LocalTrackResult

    class Base(
        private val repository: LocalTrackRepository
    ) : LocalTrackInteractor {
        override suspend fun init(): LocalTrackResult {
            return LocalTrackResult.Success(repository.localTracksList())
        }
    }
}