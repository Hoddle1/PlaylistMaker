package com.example.playlistmaker.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.player.MediaPlayerInteractor
import com.example.playlistmaker.presentation.ui.player.view_model.AudioPlayerControl
import com.example.playlistmaker.presentation.ui.player.view_model.MediaPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MusicService : Service(), AudioPlayerControl {

    private val binder = MusicServiceBinder()

    private val player: MediaPlayerInteractor by inject()

    private val _playerState = MutableStateFlow<MediaPlayerState>(MediaPlayerState.Default())
    override fun getPlayerState(): StateFlow<MediaPlayerState> = _playerState.asStateFlow()

    private var timerJob: Job? = null

    private var artistName = ""
    private var trackName = ""

    private var isInForeground = false

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        artistName = intent?.getStringExtra("artist_name") ?: ""
        trackName = intent?.getStringExtra("track_name") ?: ""

        val songUrl = intent?.getStringExtra("song_url") ?: ""

        if (songUrl.isNotEmpty()) {
            player.preparePlayer(
                url = songUrl,
                onPrependListener = {
                    _playerState.value = MediaPlayerState.Prepared()
                },
                onCompletionListener = {
                    timerJob?.cancel()
                    player.seekTo(0)
                    _playerState.value = MediaPlayerState.Prepared()
                    stopForeground()
                }
            )
        }

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun startPlayer() {
        player.startPlayer()
        _playerState.value = MediaPlayerState.Playing(player.getPlayerTime())
        startTimer()
    }

    override fun pausePlayer() {
        player.pausePlayer()
        timerJob?.cancel()
        _playerState.value = MediaPlayerState.Paused(player.getPlayerTime())
        stopForeground()
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        player.stopPlayer()
        _playerState.value = MediaPlayerState.Default()
        player.releasePlayer()
        stopForeground()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        // Регистрируем канал уведомлений
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        val contentText = listOf(artistName, trackName)
            .filter { it.isNotBlank() }
            .joinToString(" - ")

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText(contentText.ifBlank { "Воспроизведение" })
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (_playerState.value is MediaPlayerState.Playing) {
                delay(DELAY_MILLIS)
                _playerState.value = MediaPlayerState.Playing(player.getPlayerTime())
            }
        }
    }

    override fun startForeground() {
        if (isInForeground) return
        if (_playerState.value !is MediaPlayerState.Playing) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            createServiceNotification(),
            getForegroundServiceTypeConstant()
        )

        isInForeground = true

    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    override fun stopForeground() {

        if (!isInForeground) return

        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        isInForeground = false
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private companion object {
        private const val DELAY_MILLIS = 500L
        const val NOTIFICATION_ID = 1001
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
    }

}