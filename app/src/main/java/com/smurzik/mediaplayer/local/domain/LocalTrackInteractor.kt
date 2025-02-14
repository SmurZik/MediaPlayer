package com.smurzik.mediaplayer.local.domain


interface LocalTrackInteractor {

    suspend fun init(query: String): LocalTrackResult

    class Base(
        private val repository: LocalTrackRepository
    ) : LocalTrackInteractor {
        override suspend fun init(query: String): LocalTrackResult {
            return LocalTrackResult.Success(repository.localTracksList(query))
        }
    }
}