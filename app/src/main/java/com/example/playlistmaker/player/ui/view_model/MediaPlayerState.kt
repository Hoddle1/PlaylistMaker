package com.example.playlistmaker.player.ui.view_model

sealed class MediaPlayerState {
    data object Default : MediaPlayerState()
    data object Prepared : MediaPlayerState()
    data object Playing : MediaPlayerState()
    data object Paused : MediaPlayerState()
}