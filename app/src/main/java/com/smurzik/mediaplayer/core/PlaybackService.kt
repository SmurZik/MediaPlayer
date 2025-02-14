package com.smurzik.mediaplayer.core

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession.*
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.smurzik.mediaplayer.local.data.BaseLocalTrackRepository
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.domain.LocalTrackResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
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
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
        val repository =
            BaseLocalTrackRepository(LocalTrackDataSource.Base(this), LocalTrackDataToDomain())
        interactor = LocalTrackInteractor.Base(repository)
        queryMusic()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession
}