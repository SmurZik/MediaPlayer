package com.smurzik.mediaplayer.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.smurzik.mediaplayer.local.data.BaseLocalTrackRepository
import com.smurzik.mediaplayer.local.data.LocalTrackDataSource
import com.smurzik.mediaplayer.local.data.LocalTrackDataToDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.presentation.ListLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackResultMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackUiMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackViewModel
import com.smurzik.mediaplayer.local.presentation.ProgressLiveDataWrapper

class MediaPlayerApp : Application() {

    lateinit var viewModel: LocalTrackViewModel

    override fun onCreate() {
        super.onCreate()
        val listLiveDataWrapper = ListLiveDataWrapper.Base()
        viewModel = LocalTrackViewModel(
            progressLiveDataWrapper = ProgressLiveDataWrapper.Base(),
            interactor = LocalTrackInteractor.Base(
                BaseLocalTrackRepository(
                    LocalTrackDataSource.Base(this),
                    LocalTrackDataToDomain()
                )
            ),
            mapper = LocalTrackResultMapper(
                listLiveDataWrapper,
                LocalTrackUiMapper()
            ),
            listLiveDataWrapper = listLiveDataWrapper
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