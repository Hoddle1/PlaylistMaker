package com.example.playlistmaker.domain.model

data class PlayerState(val state: State) {
    enum class State {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }
}