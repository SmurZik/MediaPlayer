package com.smurzik.mediaplayer.local.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.smurzik.mediaplayer.core.PlaybackServiceHelper
import com.smurzik.mediaplayer.core.SharedTrackLiveDataWrapper
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.player.presentation.PlayerInfoUi
import com.smurzik.mediaplayer.player.presentation.SeekBarLiveDataWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalTrackViewModel(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val progressLiveDataWrapper: ProgressLiveDataWrapper.Mutable,
    private val interactor: LocalTrackInteractor,
    private val mapper: LocalTrackResultMapper,
    private val queryMapper: LocalTrackQueryMapper,
    private val musicHelper: PlaybackServiceHelper,
    private val trackProgress: SeekBarLiveDataWrapper.Mutable,
    private val currentTrack: CurrentTrackLiveDataWrapper.Mutable,
    private val mediaItemMapper: MediaItemUiMapper
) : ListLiveDataWrapper.Read, ViewModel() {

    fun init() {
        progressLiveDataWrapper.update(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            val result = interactor.init()
            progressLiveDataWrapper.update(View.GONE)
            result.map(mapper)
        }
    }

    fun searchTrack(query: String) {
        progressLiveDataWrapper.update(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            val result = interactor.searchTrack(query)
            progressLiveDataWrapper.update(View.GONE)
            result.map(queryMapper)
        }
    }

    fun changeTrack(trackIndex: Int) {
        val mediaItemList = listLiveDataWrapper.liveData().value!!.map {
            it.map(mediaItemMapper)
        }
        if (trackIndex != musicHelper.currentTrackIndex() || !musicHelper.isPlaying()) {
            val progress =
                if (trackIndex != musicHelper.currentTrackIndex()) 0 else trackProgress.liveData().value?.toLong()
                    ?: 0
            musicHelper.setMediaItemList(
                mediaItemList,
                trackIndex,
                progress
            )
        } else {
            musicHelper.playPause()
        }
    }

    fun trackLiveDataWrapper(): LiveData<Int> = currentTrack.liveData()

    fun trackProgressLiveData(): LiveData<Long> = trackProgress.liveData()

    fun progressLiveData(): LiveData<Int> = progressLiveDataWrapper.liveData()

    fun isPlaying() = musicHelper.isPlaying()

    fun currentTrackIndex() = musicHelper.currentTrackIndex()

    override fun liveData(): LiveData<List<LocalTrackUi>> = listLiveDataWrapper.liveData()

}