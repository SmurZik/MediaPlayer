package com.smurzik.mediaplayer.core

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.smurzik.mediaplayer.local.data.BaseLocalTrackRepository
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.data.LocalTrackDataToQuery
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.domain.LocalTrackResult
import com.smurzik.mediaplayer.local.presentation.LocalTrackUi
import com.smurzik.mediaplayer.local.presentation.LocalTrackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            duration: Long
        ): String {
            return trackUri
        }

    }

    override fun onCreate() {
        super.onCreate()
        viewModel = (application as MediaPlayerApp).viewModel
        mediaSession = (application as MediaPlayerApp).mediaSession
        val repository =
            BaseLocalTrackRepository(
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