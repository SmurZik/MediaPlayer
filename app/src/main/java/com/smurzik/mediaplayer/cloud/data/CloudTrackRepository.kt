package com.smurzik.mediaplayer.cloud.data

import com.smurzik.mediaplayer.favorite.data.FavoriteCloud
import com.smurzik.mediaplayer.favorite.data.FavoriteService
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.TrackRepository
import com.smurzik.mediaplayer.login.data.cache.UserDao
import java.net.ConnectException

class CloudTrackRepository(
    private val trackService: TrackService,
    private val favoriteService: FavoriteService,
    private val dao: UserDao
) : TrackRepository {
    override suspend fun tracksList(): List<LocalTrackDomain> {
        val data = trackService.chartTracks()
        val favorites: List<FavoriteCloud> = try {
            favoriteService.fetchFavorites(dao.getToken() ?: "")
        } catch (e: Exception) {
            listOf()
        }

        return data.tracks.data.map { item ->
            LocalTrackDomain(
                item.title,
                item.artist.name,
                item.album.albumCover,
                item.preview,
                29000,
                item.position - 1,
                item.album.albumTitle,
                item.id,
                favorites.map { it.id }.contains(item.id)
            )
        }
    }

    override suspend fun searchTrack(query: String): List<LocalTrackDomain> {
        val tracks = trackService.searchTrack(query)
        val list = tracks.data
        val resultList = mutableListOf<LocalTrackDomain>()
        val favorites: List<FavoriteCloud> = try {
            favoriteService.fetchFavorites(dao.getToken() ?: "")
        } catch (e: Exception) {
            listOf()
        }
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
                    trackContent.id,
                    favorites.map { it.id }.contains(trackContent.id)
                )
            )
        }
        return resultList
    }

    override suspend fun changeFavorite(track: LocalTrackDomain) = Unit
}