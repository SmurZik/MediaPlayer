package com.smurzik.mediaplayer.local.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.smurzik.mediaplayer.core.PlaybackServiceHelper
import com.smurzik.mediaplayer.core.SharedTrackLiveDataWrapper
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.player.presentation.PlayerInfoUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalTrackViewModel(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val queryLiveDataWrapper: QueryLiveDataWrapper.Mutable,
    private val sharedTrackLiveDataWrapper: SharedTrackLiveDataWrapper.Mutable,
    private val progressLiveDataWrapper: ProgressLiveDataWrapper.Mutable,
    private val interactor: LocalTrackInteractor,
    private val mapper: LocalTrackResultMapper,
    private val queryMapper: LocalTrackQueryMapper,
    private val musicHelper: PlaybackServiceHelper
) : ListLiveDataWrapper.Read, ViewModel() {

    fun init(query: String, isSearch: Boolean) {
        progressLiveDataWrapper.update(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            val result = interactor.init(query)
            progressLiveDataWrapper.update(View.GONE)
            if (isSearch) {
                result.map(queryMapper)
                CoroutineScope(Dispatchers.Main).launch {
                    musicHelper.onMediaStateEvents("spec", isSearching = query.isNotEmpty())
                }
            } else
                result.map(mapper)
        }
    }

    fun isPlaying() = musicHelper.isPlaying()

    fun currentTrackIndex() = musicHelper.currentTrackIndex()

    fun changeTrack(trackIndex: Int, isSearching: Boolean, trackInfo: PlayerInfoUi) {
        sharedTrackLiveDataWrapper.update(trackInfo)
        musicHelper.onMediaStateEvents("", trackIndex, 0, isSearching)
    }

    override fun liveData(): LiveData<List<LocalTrackUi>> = listLiveDataWrapper.liveData()

}