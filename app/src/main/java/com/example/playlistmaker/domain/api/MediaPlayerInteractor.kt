package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.PlayerState

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
    fun getState(): PlayerState
    fun setState(state: PlayerState)
}