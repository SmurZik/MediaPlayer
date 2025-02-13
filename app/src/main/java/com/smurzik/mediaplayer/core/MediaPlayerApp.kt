package com.smurzik.mediaplayer.core

import android.app.Application
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
    }
}