package com.smurzik.mediaplayer.core

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.smurzik.mediaplayer.cloud.data.CloudTrackRepository
import com.smurzik.mediaplayer.cloud.data.TrackService
import com.smurzik.mediaplayer.cloud.presentation.CloudViewModel
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.data.LocalTrackDataToQuery
import com.smurzik.mediaplayer.local.data.LocalTrackRepository
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.presentation.CurrentTrackLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.ListLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackQueryMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackResultMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackUiMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackViewModel
import com.smurzik.mediaplayer.local.presentation.MediaItemUiMapper
import com.smurzik.mediaplayer.local.presentation.ProgressLiveDataWrapper
import com.smurzik.mediaplayer.player.presentation.PlayerViewModel
import com.smurzik.mediaplayer.player.presentation.SeekBarLiveDataWrapper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MediaPlayerApp : Application() {

    lateinit var viewModel: LocalTrackViewModel
    lateinit var cloudViewModel: CloudViewModel
    lateinit var playerViewModel: PlayerViewModel
    lateinit var mediaSession: MediaSession
    private lateinit var service: TrackService

    override fun onCreate() {
        super.onCreate()
        val listLiveDataWrapper = ListLiveDataWrapper.Base()
        val sharedTrackLiveDataWrapper = SharedTrackLiveDataWrapper.Base()
        val exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
        val serviceHelper = PlaybackServiceHelper(exoPlayer)
        val seekBarLiveDataWrapper = SeekBarLiveDataWrapper.Base()
        val currentTrackLiveDataWrapper = CurrentTrackLiveDataWrapper.Base()

        service = Retrofit.Builder().baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(TrackService::class.java)

        val mapper = LocalTrackUiMapper()

        cloudViewModel = CloudViewModel(
            progressLiveDataWrapper = ProgressLiveDataWrapper.Base(),
            interactor = LocalTrackInteractor.Base(
                CloudTrackRepository(
                    service
                )
            ),
            mapper = LocalTrackResultMapper(
                listLiveDataWrapper,
                mapper
            ),
            listLiveDataWrapper = listLiveDataWrapper,
            musicHelper = serviceHelper,
            queryMapper = LocalTrackQueryMapper(
                listLiveDataWrapper,
                mapper
            ),
            trackProgress = seekBarLiveDataWrapper,
            currentTrack = currentTrackLiveDataWrapper,
            mediaItemMapper = MediaItemUiMapper()
        )

        viewModel = LocalTrackViewModel(
            progressLiveDataWrapper = ProgressLiveDataWrapper.Base(),
            interactor = LocalTrackInteractor.Base(
                LocalTrackRepository(
                    LocalTrackDataSource.Base(this),
                    LocalTrackDataToDomain(),
                    LocalTrackDataToQuery()
                )
            ),
            mapper = LocalTrackResultMapper(
                listLiveDataWrapper,
                mapper
            ),
            listLiveDataWrapper = listLiveDataWrapper,
            musicHelper = serviceHelper,
            queryMapper = LocalTrackQueryMapper(
                listLiveDataWrapper,
                mapper
            ),
            trackProgress = seekBarLiveDataWrapper,
            currentTrack = currentTrackLiveDataWrapper,
            mediaItemMapper = MediaItemUiMapper()
        )
        playerViewModel = PlayerViewModel(
            sharedTrackLiveDataWrapper = sharedTrackLiveDataWrapper,
            seekBarLiveDataWrapper = seekBarLiveDataWrapper,
            musicHelper = serviceHelper,
            sharedAllTracksLiveDataWrapper = listLiveDataWrapper,
            currentTrack = currentTrackLiveDataWrapper
        )
    }
}