package com.example.playlistmaker.presentation.ui.player.view_model

import com.example.playlistmaker.domain.entity.Playlist

sealed interface BottomSheetState {
    object Hidden : BottomSheetState
    data class Collapsed(val playlists: List<Playlist>) : BottomSheetState
}