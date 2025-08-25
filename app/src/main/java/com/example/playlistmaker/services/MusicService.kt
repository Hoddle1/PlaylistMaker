package com.example.playlistmaker.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
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

    override fun onBind(intent: Intent?): IBinder {
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
                }
            )
        }

        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
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
    }

    private fun releasePlayer() {
        timerJob?.cancel()
        player.stopPlayer()
        _playerState.value = MediaPlayerState.Default()
        player.releasePlayer()
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (_playerState.value is MediaPlayerState.Playing) {
                delay(DELAY_MILLIS)
                _playerState.value = MediaPlayerState.Playing(player.getPlayerTime())
            }
        }
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private companion object {
        private const val DELAY_MILLIS = 500L
    }

}