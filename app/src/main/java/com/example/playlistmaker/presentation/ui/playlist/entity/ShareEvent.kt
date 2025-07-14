package com.example.playlistmaker.presentation.ui.playlist.entity

sealed class ShareEvent {
    data class ShowToast(val message: String) : ShareEvent()
    data class ShareText(val text: String) : ShareEvent()
}