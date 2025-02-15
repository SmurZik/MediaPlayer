package com.smurzik.mediaplayer.core

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.RepeatMode

class PlaybackServiceHelper(
    private val exoPlayer: Player
) : Player.Listener {

    init {
        exoPlayer.addListener(this)
    }

    private var currentIndex = 0

    private val listenerSearchTrack = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_SEEK) {
                exoPlayer.seekTo(currentIndex, 0)
            }
        }
    }

    fun setMediaItemList(mediaList: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaList)
        exoPlayer.prepare()
    }

    fun setMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun onMediaStateEvents(
        mediaStateEvents: String,
        selectedMusicIndex: Int = -1,
        seekPosition: Long = 0,
        isSearching: Boolean
    ) {
        if (selectedMusicIndex == exoPlayer.currentMediaItemIndex) {
            playPause()
        } else {
            exoPlayer.seekToDefaultPosition(selectedMusicIndex)
            currentIndex = selectedMusicIndex
            exoPlayer.play()
        }
        if (isSearching) {
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            exoPlayer.addListener(listenerSearchTrack)
        } else {
            exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            exoPlayer.removeListener(listenerSearchTrack)
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