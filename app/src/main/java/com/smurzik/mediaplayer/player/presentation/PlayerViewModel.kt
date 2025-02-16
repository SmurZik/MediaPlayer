package com.smurzik.mediaplayer.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smurzik.mediaplayer.core.PlaybackServiceHelper
import com.smurzik.mediaplayer.core.SharedTrackLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.CurrentTrackLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.ListLiveDataWrapper
import com.smurzik.mediaplayer.local.presentation.LocalTrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val sharedAllTracksLiveDataWrapper: ListLiveDataWrapper.Mutable,
    private val sharedTrackLiveDataWrapper: SharedTrackLiveDataWrapper.Mutable,
    private val seekBarLiveDataWrapper: SeekBarLiveDataWrapper.Mutable,
    private val musicHelper: PlaybackServiceHelper,
    private val currentTrack: CurrentTrackLiveDataWrapper.Mutable
) : ViewModel(), SharedTrackLiveDataWrapper.Read {

    private var seekBarJob: Job? = null

    override fun liveData(): LiveData<PlayerInfoUi> = sharedTrackLiveDataWrapper.liveData()

    fun seekBarLiveDataWrapper(): LiveData<Long> = seekBarLiveDataWrapper.liveData()

    fun updateSeekBar() {
        if (seekBarJob?.isActive == true) return

        seekBarJob?.cancel()
        seekBarJob = viewModelScope.launch {
            while (musicHelper.isPlaying()) {
                seekBarLiveDataWrapper.update(musicHelper.currentProgress())
                delay(1000)
            }
        }
    }

    fun playPause() {
        musicHelper.playPause()
    }

    fun nextTrack() {
        musicHelper.nextTrack()
    }

    fun previousTrack() {
        musicHelper.previousTrack()
    }

    fun changeTrackProgress(progress: Int) {
        musicHelper.changeTrackProgress(progress)
    }

    fun updateCurrentTrack(index: Int) {
        val track = sharedAllTracksLiveDataWrapper.liveData().value?.get(index) ?: LocalTrackUi(
            "", "", "", "", 0, -1, "", -1
        )
        sharedTrackLiveDataWrapper.update(
            PlayerInfoUi(
                track.albumUri,
                track.title,
                track.author,
                track.duration,
                track.album
            )
        )

        currentTrack.update(musicHelper.currentTrackIndex())
    }
}