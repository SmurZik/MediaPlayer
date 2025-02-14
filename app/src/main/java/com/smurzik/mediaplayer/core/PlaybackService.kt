package com.smurzik.mediaplayer.core

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.smurzik.mediaplayer.local.data.BaseLocalTrackRepository
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.domain.LocalTrackResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaybackService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private lateinit var interactor: LocalTrackInteractor
    private var trackList = mutableListOf<MediaItem>()

    private fun queryMusic() {
        CoroutineScope(Dispatchers.IO).launch {
            trackList.clear()
            val tracks = interactor.init().map(Mapper())
            trackList.addAll(tracks.map { MediaItem.fromUri(Uri.parse(it)) })
        }
    }

    inner class Mapper : LocalTrackResult.Mapper<List<String>> {
        override fun map(list: List<LocalTrackDomain>, errorMessage: String): List<String> {
            return list.map { it.trackUri }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = (application as MediaPlayerApp).mediaSession
        val repository =
            BaseLocalTrackRepository(LocalTrackDataSource.Base(this), LocalTrackDataToDomain())
        interactor = LocalTrackInteractor.Base(repository)
        queryMusic()
    }

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession
}