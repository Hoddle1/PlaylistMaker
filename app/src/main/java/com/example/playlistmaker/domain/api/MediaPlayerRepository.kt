package com.example.playlistmaker.domain.api

interface MediaPlayerRepository {
    fun preparePlayer(
        url: String,
        onPrependListener: () -> Unit,
        onCompletionListener: () -> Unit
    )

    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
}