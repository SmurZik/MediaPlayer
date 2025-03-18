package com.smurzik.mediaplayer.favorite.data

import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.TrackRepository
import com.smurzik.mediaplayer.login.data.cache.UserDao

class FavoriteCloudRepository(
    private val favoriteService: FavoriteService,
    private val dao: UserDao
) : TrackRepository {
    override suspend fun tracksList(): List<LocalTrackDomain> {

        val data = favoriteService.fetchFavorites(dao.getToken() ?: "")
        return data.map {
            LocalTrackDomain(
                it.title,
                it.artist,
                it.albumUri,
                it.uri,
                it.duration,
                it.index,
                it.album,
                it.id,
                true
            )
        }
    }

    override suspend fun searchTrack(query: String): List<LocalTrackDomain> {
        TODO()
    }

    override suspend fun changeFavorite(track: LocalTrackDomain) {
        val trackCloud = FavoriteCloud(
            track.id,
            track.title,
            track.author,
            track.album,
            track.duration,
            track.trackUri,
            track.albumUri,
            track.index
        )
        favoriteService.favoriteChange(dao.getToken() ?: "", trackCloud)
    }
}