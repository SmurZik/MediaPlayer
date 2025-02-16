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

    fun currentTrackIndex() = exoPlayer.currentMediaItemIndex

    fun changeTrackProgress(progress: Int) = exoPlayer.seekTo(progress.toLong())

    fun isPlaying() = exoPlayer.isPlaying

    fun currentProgress(): Long = exoPlayer.currentPosition

    fun nextTrack() = exoPlayer.seekToNext()

    fun previousTrack() = exoPlayer.seekToPrevious()

    fun setMediaItemList(mediaList: List<MediaItem>, selectedMusicIndex: Int, progress: Long) {
        exoPlayer.setMediaItems(mediaList)
        exoPlayer.prepare()
        exoPlayer.seekTo(selectedMusicIndex, progress)
        if (selectedMusicIndex == exoPlayer.currentMediaItemIndex) {
            playPause()
        } else {
            exoPlayer.seekToDefaultPosition(selectedMusicIndex)
            currentIndex = selectedMusicIndex
            exoPlayer.play()
        }
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }
}