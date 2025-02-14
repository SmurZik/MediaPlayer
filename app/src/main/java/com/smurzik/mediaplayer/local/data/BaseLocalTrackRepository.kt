package com.smurzik.mediaplayer.local.data

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackRepository

class BaseLocalTrackRepository(
    private val localDataSource: LocalTrackDataSource,
    private val mapperToDomain: LocalTrackData.Mapper<LocalTrackDomain>,
    private val mapperToQuery: LocalTrackData.Mapper<Pair<String, String>>
) : LocalTrackRepository {

    override suspend fun localTracksList(query: String): List<LocalTrackDomain> {
        val data = localDataSource.allTracks()
        return if (query.isNotEmpty())
            data.filter {
                it.map(mapperToQuery).first.startsWith(query, ignoreCase = true) ||
                        it.map(mapperToQuery).second.startsWith(query, ignoreCase = true)
            }.map { it.map(mapperToDomain) }
        else {
            data.map { it.map(mapperToDomain) }
        }
    }
}