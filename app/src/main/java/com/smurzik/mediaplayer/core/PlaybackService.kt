package com.smurzik.mediaplayer.core

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.smurzik.mediaplayer.local.data.LocalTrackRepository
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.data.LocalTrackDataToQuery
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.presentation.LocalTrackUi
import com.smurzik.mediaplayer.local.presentation.LocalTrackViewModel

class PlaybackService : MediaSessionService() {

    private lateinit var mediaSession: MediaSession
    private lateinit var interactor: LocalTrackInteractor
    private var trackList = mutableListOf<MediaItem>()
    private lateinit var viewModel: LocalTrackViewModel

    private fun queryMusic() {
        trackList.clear()
        val tracks = viewModel.liveData().value?.map { it.map(Mapper()) } ?: listOf()
        trackList.addAll(tracks.map { MediaItem.fromUri(Uri.parse(it)) })
    }

    inner class Mapper : LocalTrackUi.Mapper<String> {
        override fun map(
            title: String,
            author: String,
            albumUri: String,
            trackUri: String,
            duration: Long,
            index: Int,
            album: String,
            id: Long
        ): String {
            return trackUri
        }

    }

    override fun onCreate() {
        super.onCreate()
        viewModel = (application as MediaPlayerApp).viewModel
        val service = (application as MediaPlayerApp).service
        mediaSession = (application as MediaPlayerApp).mediaSession
        val repository =
            LocalTrackRepository(
                LocalTrackDataSource.Base(this),
                LocalTrackDataToDomain(),
                LocalTrackDataToQuery()
            )
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