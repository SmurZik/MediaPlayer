package com.smurzik.mediaplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    private val _progressFlow = MutableStateFlow(0)
    val progressFlow: StateFlow<Int> = _progressFlow

    private val localBroadcastManager = LocalBroadcastManager.getInstance(this)
    private val progressIntent = Intent(PROGRESS_UPDATE)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
                val trackUri = intent.getStringExtra(EXTRA_TRACK_URI)?.toUri()
                trackUri?.let {
                    if (mediaPlayer == null) {
                        playMusic(it)
                        showNotification()
                    }
                    resumeMusic()
                }
            }

            Actions.STOP.toString() -> {
                pauseMusic()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Проигрывание")
            .setContentText("Kick Dat Shit Remix")
            .setProgress(100, 50, false)
            .build()
        startForeground(1, notification)

    }

    enum class Actions {
        START, STOP
    }

    fun playMusic(uri: Uri) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@PlayerService, uri)
            prepare()
        }
    }

    fun pauseMusic() {
        Log.d("smurzLog", "pause")
        mediaPlayer?.pause()
    }

    fun resumeMusic() {
        Log.d("smurzLog", "resuming mp: $mediaPlayer")
        startProgressUpdates()
        mediaPlayer?.start()
    }

    private fun startProgressUpdates() {
        CoroutineScope(Dispatchers.IO).launch {
            while (mediaPlayer?.isPlaying == true) {
                _progressFlow.value = (mediaPlayer?.currentPosition?.div(1000)) ?: 0
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
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        const val EXTRA_PROGRESS = "EXTRA_PROGRESS"
        const val EXTRA_TRACK_URI = "EXTRA_TRACK_URI"
        const val PROGRESS_UPDATE = "PROGRESS_UPDATE"
    }
}