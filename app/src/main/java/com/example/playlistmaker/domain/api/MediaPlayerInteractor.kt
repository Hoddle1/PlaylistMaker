package com.example.playlistmaker.domain.api

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