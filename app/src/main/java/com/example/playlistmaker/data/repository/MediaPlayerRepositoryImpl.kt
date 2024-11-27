package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerRepository

class MediaPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : MediaPlayerRepository {
    override fun preparePlayer(
        url: String,
        onPrependListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            onPrependListener()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletionListener()
        }

    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun stopPlayer() {
        mediaPlayer.stop()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}