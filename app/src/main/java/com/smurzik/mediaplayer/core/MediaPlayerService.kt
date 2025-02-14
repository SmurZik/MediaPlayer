package com.smurzik.mediaplayer.core

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.smurzik.mediaplayer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaPlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val _progressFlow = MutableStateFlow(0)
    private var title = ""
    private var author = ""
    private var albumUri = ""
    private var job: Job? = null
    private var updateNotificationJob: Job? = null
    private var duration = 0
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
        mediaPlayer.release()
        super.onTaskRemoved(rootIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> {
                val data = intent.getStringArrayExtra(EXTRA_TRACK_URI)
                data?.let {
                    val trackUri = data[0]
                    title = data[1]
                    author = data[2]
                    duration = data[3].toInt()
                    albumUri = data[4]
                    playTrack(trackUri)
                }
            }

            Actions.NEXT.toString() -> {
                nextTrack()
            }

            Actions.PLAY_PAUSE.toString() -> {
                if (mediaPlayer.isPlaying) pauseMusic() else resumeMusic()
            }

            Actions.STOP.toString() -> {
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun nextTrack() {

    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MediaPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotification(
        progress: Int,
        title: String,
        artist: String,
        duration: Int,
        albumUri: String
    ) {
        updateNotificationJob = CoroutineScope(Dispatchers.IO).launch {
            val notification =
                NotificationCompat.Builder(this@MediaPlayerService, "running_channel")
                    .setSmallIcon(R.drawable.ic_music_note)
                    .addAction(
                        R.drawable.ic_skip_previous,
                        null,
                        getPendingIntent(Actions.PREVIOUS.toString())
                    )
                    .addAction(
                        if (mediaPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                        null,
                        getPendingIntent(Actions.PLAY_PAUSE.toString())
                    )
                    .addAction(
                        R.drawable.ic_skip_next,
                        null,
                        getPendingIntent(Actions.NEXT.toString())
                    )
                    .setStyle(
                        androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                    )
                    .setContentTitle(title)
                    .setContentText(artist)
                    .setProgress(duration, progress, false)
                    .setLargeIcon(getLargeIconGlide(Uri.parse(albumUri)))
                    .build()
            startForeground(1, notification)
        }
    }

    private suspend fun getLargeIconGlide(coverUri: Uri?): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                coverUri?.let { uri ->
                    Glide.with(this@MediaPlayerService)
                        .asBitmap()
                        .load(uri)
                        .submit()
                        .get()
                }
            } catch (e: Exception) {
                BitmapFactory.decodeResource(
                    this@MediaPlayerService.resources,
                    R.drawable.ic_music_note
                )
            }
        }
    }

    enum class Actions {
        START, STOP, PREVIOUS, NEXT, PLAY_PAUSE
    }

    private fun playTrack(trackUri: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(this, Uri.parse(trackUri))
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            job?.cancel()
            updateNotificationJob?.cancel()
            startProgressUpdates()
            mediaPlayer.start()
        }
    }

    private fun pauseMusic() {
        mediaPlayer.pause()
        createNotification(_progressFlow.value, title, author, duration, albumUri)
    }

    private fun resumeMusic() {
        startProgressUpdates()
        mediaPlayer.start()
    }

    private fun startProgressUpdates() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (mediaPlayer.isPlaying) {
                _progressFlow.value = (mediaPlayer.currentPosition.div(1000))
                sendProgressUpdate(_progressFlow.value)
                createNotification(_progressFlow.value, title, author, duration, albumUri)
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