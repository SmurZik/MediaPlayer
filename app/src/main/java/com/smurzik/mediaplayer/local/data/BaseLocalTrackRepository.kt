package com.smurzik.mediaplayer.local.data

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackRepository

class BaseLocalTrackRepository(
    private val localDataSource: LocalTrackDataSource,
    private val mapperToDomain: LocalTrackData.Mapper<LocalTrackDomain>
) : LocalTrackRepository {

    override suspend fun localTracksList(): List<LocalTrackDomain> {
        val data = localDataSource.allTracks()
        return data.map { it.map(mapperToDomain) }
    }
}