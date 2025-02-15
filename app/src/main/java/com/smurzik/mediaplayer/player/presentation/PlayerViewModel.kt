package com.smurzik.mediaplayer.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smurzik.mediaplayer.core.PlaybackServiceHelper
import com.smurzik.mediaplayer.core.SharedTrackLiveDataWrapper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val sharedTrackLiveDataWrapper: SharedTrackLiveDataWrapper.Mutable,
    private val seekBarLiveDataWrapper: SeekBarLiveDataWrapper.Mutable,
    private val musicHelper: PlaybackServiceHelper
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

    fun updateCurrentTrack(value: PlayerInfoUi) {
        sharedTrackLiveDataWrapper.update(value)
    }
}