package com.smurzik.mediaplayer.local.data

import com.smurzik.mediaplayer.favorite.data.FavoriteCloud
import com.smurzik.mediaplayer.favorite.data.FavoriteService
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.TrackRepository
import com.smurzik.mediaplayer.login.data.cache.UserDao

class LocalTrackRepository(
    private val localDataSource: LocalTrackDataSource,
    private val mapperToDomain: LocalTrackData.Mapper<LocalTrackDomain>,
    private val mapperToQuery: LocalTrackData.Mapper<Pair<String, String>>,
    private val favoriteService: FavoriteService,
    private val dao: UserDao
) : TrackRepository {

    override suspend fun searchTrack(query: String): List<LocalTrackDomain> {
        val data = localDataSource.allTracks(query)
        val favorites: List<FavoriteCloud> = try {
            favoriteService.fetchFavorites(dao.getToken() ?: "")
        } catch (e: Exception) {
            listOf()
        }
        return data.filter {
            it.map(mapperToQuery).first.startsWith(query, ignoreCase = true) ||
                    it.map(mapperToQuery).second.startsWith(query, ignoreCase = true)
        }.map { item ->
            LocalTrackDomain(
                item.title,
                item.author,
                item.albumUri,
                item.trackUri,
                item.duration,
                item.index,
                item.album,
                item.id,
                favorites.map { it.id }.contains(item.id)
            )
        }
    }

    override suspend fun changeFavorite(track: LocalTrackDomain) = Unit

    override suspend fun tracksList(): List<LocalTrackDomain> {
        val data = localDataSource.allTracks("")
        val favorites: List<FavoriteCloud> = try {
            favoriteService.fetchFavorites(dao.getToken() ?: "")
        } catch (e: Exception) {
            listOf()
        }
        return data.map { item ->
            LocalTrackDomain(
                item.title,
                item.author,
                item.albumUri,
                item.trackUri,
                item.duration,
                item.index,
                item.album,
                item.id,
                favorites.map { it.id }.contains(item.id)
            )
        }
    }
}