package com.smurzik.mediaplayer.local.domain


interface LocalTrackInteractor {

    suspend fun init(): LocalTrackResult

    suspend fun searchTrack(query: String): LocalTrackResult

    suspend fun changeFavorite(track: LocalTrackDomain)

    class Base(
        private val repository: TrackRepository
    ) : LocalTrackInteractor {
        override suspend fun init(): LocalTrackResult {
            try {
                val result = repository.tracksList()
                return LocalTrackResult.Success(result)
            } catch (e: Exception) {
                return LocalTrackResult.Failure(e.message ?: "")
            }
        }

        override suspend fun searchTrack(query: String): LocalTrackResult {
            try {
                val result = repository.searchTrack(query)
                return LocalTrackResult.Success(result)
            } catch (e: Exception) {
                return LocalTrackResult.Failure(e.message ?: "")
            }
        }

        override suspend fun changeFavorite(track: LocalTrackDomain) {
            repository.changeFavorite(track)
        }
    }
}