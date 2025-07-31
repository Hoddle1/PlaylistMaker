package com.example.playlistmaker.presentation.ui.playlist.entity

sealed class UiEvent {
    data class ShowDialog(
        val message: String,
        val onConfirm: () -> Unit,
        val onCancel: () -> Unit
    ) : UiEvent()

    object DismissDialog : UiEvent()

    object CloseScreen : UiEvent()
}