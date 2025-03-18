package com.smurzik.mediaplayer.favorite.presentation

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smurzik.mediaplayer.core.PlaybackServiceHelper
import com.smurzik.mediaplayer.local.domain.LocalTrackDomain
import com.smurzik.mediaplayer.local.domain.LocalTrackInteractor
import com.smurzik.mediaplayer.local.presentation.CurrentTrackLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.ListLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackQueryMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackResultMapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackUi
import com.smurzik.mediaplayer.local.presentation.MediaItemUiMapper
import com.smurzik.mediaplayer.local.presentation.ProgressLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.ShowErrorLiveDataWrapper
import com.smurzik.mediaplayer.player.presentation.SeekBarLiveDataWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val listLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val progressLiveDataWrapper: ProgressLiveDataWrapper.Mutable,
    private val interactor: LocalTrackInteractor,
    private val mapper: LocalTrackResultMapper,
    private val queryMapper: LocalTrackQueryMapper,
    private val musicHelper: PlaybackServiceHelper,
    private val trackProgress: SeekBarLiveDataWrapper.Mutable,
    private val currentTrack: CurrentTrackLiveDataWrapper.Mutable,
    private val mediaItemMapper: MediaItemUiMapper,
    private val showErrorLiveDataWrapper: ShowErrorLiveDataWrapper.Mutable
) : ListLiveDataWrapper.Read, ViewModel() {

    private val _favoriteLiveData = MutableLiveData<LocalTrackUi>()
    val favoriteLiveData: LiveData<LocalTrackUi> = _favoriteLiveData

    fun init() {
        progressLiveDataWrapper.update(View.VISIBLE)
        viewModelScope.launch(Dispatchers.IO) {
            val result = interactor.init()
            progressLiveDataWrapper.update(View.GONE)
            result.map(mapper)
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

    fun changeFavorite(track: LocalTrackUi, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val trackDomain = LocalTrackDomain(
                track.title,
                track.author,
                track.albumUri,
                track.trackUri,
                track.duration,
                track.index,
                track.album,
                track.id,
                track.isFavorite
            )
            interactor.changeFavorite(trackDomain)
            val notNullList = listLiveDataWrapper.liveData().value ?: emptyList()
            val updatedList =
                notNullList.map {
                    if (it.id == track.id) {
                        it.copy(isFavorite = !track.isFavorite)
                    } else it
                }
            listLiveDataWrapper.update(updatedList)
            if (isFavorite)
                init()
        }
    }

    fun showError(): LiveData<Int> = showErrorLiveDataWrapper.liveData()

    fun progressLiveData(): LiveData<Int> = progressLiveDataWrapper.liveData()

    fun isPlaying() = musicHelper.isPlaying()

    fun currentTrackIndex() = musicHelper.currentTrackIndex()

    override fun liveData(): LiveData<List<LocalTrackUi>> = listLiveDataWrapper.liveData()
}