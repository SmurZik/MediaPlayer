package com.smurzik.mediaplayer.core

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

class PlaybackServiceHelper(
    private val exoPlayer: Player
) : Player.Listener {

    init {
        exoPlayer.addListener(this)
    }

    fun setMediaItemList(mediaList: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaList)
        exoPlayer.prepare()
    }

    fun onMediaStateEvents(
        mediaStateEvents: String,
        selectedMusicIndex: Int = -1,
        seekPosition: Long = 0
    ) {
        if (selectedMusicIndex == exoPlayer.currentMediaItemIndex) {
            playPause()
        } else {
            exoPlayer.seekToDefaultPosition(selectedMusicIndex)
            exoPlayer.play()
        }
    }

    private fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }
}