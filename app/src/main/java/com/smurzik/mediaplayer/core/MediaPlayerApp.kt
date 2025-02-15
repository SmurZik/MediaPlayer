package com.smurzik.mediaplayer.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.smurzik.mediaplayer.local.data.BaseLocalTrackRepository
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.data.LocalTrackDataToQuery
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.presentation.ListLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackQueryMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackResultMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackUiMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackViewModel
import com.smurzik.mediaplayer.local.presentation.MediaItemMapper
import com.smurzik.mediaplayer.local.presentation.ProgressLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.QueryLiveDataWrapper

class MediaPlayerApp : Application() {

    lateinit var viewModel: LocalTrackViewModel
    lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        val listLiveDataWrapper = ListLiveDataWrapper.Base()

        val exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()
        val serviceHelper = PlaybackServiceHelper(exoPlayer)

        val mapper = LocalTrackUiMapper()

        viewModel = LocalTrackViewModel(
            progressLiveDataWrapper = ProgressLiveDataWrapper.Base(),
            interactor = LocalTrackInteractor.Base(
                BaseLocalTrackRepository(
                    LocalTrackDataSource.Base(this),
                    LocalTrackDataToDomain(),
                    LocalTrackDataToQuery()
                )
            ),
            mapper = LocalTrackResultMapper(
                listLiveDataWrapper,
                mapper,
                serviceHelper,
                MediaItemMapper()
            ),
            listLiveDataWrapper = listLiveDataWrapper,
            musicHelper = serviceHelper,
            queryLiveDataWrapper = QueryLiveDataWrapper.Base(),
            queryMapper = LocalTrackQueryMapper(
                listLiveDataWrapper,
                mapper
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running_channel",
                "Running Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}