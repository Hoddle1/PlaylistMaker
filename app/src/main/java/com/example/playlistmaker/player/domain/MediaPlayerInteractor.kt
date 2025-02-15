package com.example.playlistmaker.player.domain

interface MediaPlayerInteractor {
    fun preparePlayer(
        url: String,
        onPrependListener: () -> Unit,
        onCompletionListener: () -> Unit
    )

    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun releasePlayer()
    fun getPlayerTime(): String
}