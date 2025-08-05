package com.example.playlistmaker.presentation.ui.player.view_model

sealed class MediaPlayerState(val progress: String) {
    class Default : MediaPlayerState("00:00")
    class Prepared : MediaPlayerState("00:00")
    class Playing(progress: String) : MediaPlayerState(progress)
    class Paused(progress: String) : MediaPlayerState(progress)
}