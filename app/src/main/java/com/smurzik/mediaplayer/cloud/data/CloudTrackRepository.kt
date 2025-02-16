package com.smurzik.mediaplayer.cloud.data

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.TrackRepository

class CloudTrackRepository(
    private val trackService: TrackService
) : TrackRepository {
    override suspend fun tracksList(): List<LocalTrackDomain> {
        val data = trackService.chartTracks()
        return data.tracks.data.map {
            LocalTrackDomain(
                it.title,
                it.artist.name,
                it.album.albumCover,
                it.preview,
                29000,
                it.position - 1,
                it.album.albumTitle,
                it.id
            )
        }
    }

    override suspend fun searchTrack(query: String): List<LocalTrackDomain> {
        val tracks = trackService.searchTrack(query)
        val list = tracks.data
        val resultList = mutableListOf<LocalTrackDomain>()
        list.forEachIndexed { index, trackContent ->
            resultList.add(
                LocalTrackDomain(
                    trackContent.title,
                    trackContent.artist.name,
                    trackContent.album.albumCover,
                    trackContent.preview,
                    29000,
                    index,
                    trackContent.album.albumTitle,
                    trackContent.id
                )
            )
        }
        return resultList
    }
}