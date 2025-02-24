package com.example.playlistmaker.domain.player

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
    fun seekTo(position: Int)
    fun getPlayerTime(): String
}