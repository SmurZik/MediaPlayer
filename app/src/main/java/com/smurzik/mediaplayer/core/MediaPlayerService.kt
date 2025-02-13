package com.smurzik.mediaplayer.core

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.smurzik.mediaplayer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MediaPlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val _progressFlow = MutableStateFlow(0)
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private val progressIntent = Intent(PROGRESS_UPDATE)
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
                val trackUri = intent.getStringExtra(EXTRA_TRACK_URI)
                trackUri?.let {
                    showNotification()
                    playTrack(trackUri)
                }
            }

            Actions.STOP.toString() -> {
                pauseMusic()
            }
        }
        return START_NOT_STICKY
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Kick Dat Shit Remix")
            .setContentText("8Ball, MJG")
            .setProgress(100, 50, false)
            .build()
        startForeground(1, notification)
    }

    enum class Actions {
        START, STOP
    }

    private fun playTrack(trackUri: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(this, Uri.parse(trackUri))
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
    }

    private fun resumeMusic() {
        startProgressUpdates()
        mediaPlayer.start()
    }

    private fun startProgressUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            while (mediaPlayer.isPlaying) {
                _progressFlow.value = (mediaPlayer.currentPosition.div(1000))
                sendProgressUpdate(_progressFlow.value)
                delay(1000)
            }
        }
    }

    private fun sendProgressUpdate(progress: Int) {
        progressIntent.putExtra(EXTRA_PROGRESS, progress)
        localBroadcastManager.sendBroadcast(progressIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val EXTRA_TRACK_URI = "EXTRA_TRACK_URI"
        const val PROGRESS_UPDATE = "PROGRESS_UPDATE"
    }
}