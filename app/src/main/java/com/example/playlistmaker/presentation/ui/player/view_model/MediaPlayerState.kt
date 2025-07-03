package com.example.playlistmaker.presentation.ui.player.view_model

import com.example.playlistmaker.R

sealed class MediaPlayerState(val imageResource: Int, val progress: String) {
    class Default : MediaPlayerState(R.drawable.play_button, "00:00")
    class Prepared : MediaPlayerState(R.drawable.play_button, "00:00")
    class Playing(progress: String) : MediaPlayerState(R.drawable.stop_button, progress)
    class Paused(progress: String) : MediaPlayerState(R.drawable.play_button, progress)
}