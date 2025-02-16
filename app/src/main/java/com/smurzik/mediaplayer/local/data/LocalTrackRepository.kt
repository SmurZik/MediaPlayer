package com.smurzik.mediaplayer.local.data

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.TrackRepository

class LocalTrackRepository(
    private val localDataSource: LocalTrackDataSource,
    private val mapperToDomain: LocalTrackData.Mapper<LocalTrackDomain>,
    private val mapperToQuery: LocalTrackData.Mapper<Pair<String, String>>,
) : TrackRepository {

    override suspend fun searchTrack(query: String): List<LocalTrackDomain> {
        val data = localDataSource.allTracks(query)
        return data.filter {
            it.map(mapperToQuery).first.startsWith(query, ignoreCase = true) ||
                    it.map(mapperToQuery).second.startsWith(query, ignoreCase = true)
        }.map { it.map(mapperToDomain) }
    }

    override suspend fun tracksList(): List<LocalTrackDomain> {
        val data = localDataSource.allTracks("")
        return data.map {
            it.map(mapperToDomain)
        }
    }
}