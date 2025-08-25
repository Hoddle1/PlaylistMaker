package com.example.playlistmaker.presentation.ui.player.view_model

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<MediaPlayerState>
    fun startPlayer()
    fun pausePlayer()
    fun startForeground()
    fun stopForeground()
}