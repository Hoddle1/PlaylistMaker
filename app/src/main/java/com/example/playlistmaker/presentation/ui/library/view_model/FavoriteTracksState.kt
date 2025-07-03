package com.example.playlistmaker.presentation.ui.library.view_model

import com.example.playlistmaker.domain.entity.Track


sealed class FavoriteTracksState {
    class Empty : FavoriteTracksState()
    class Content(val tracks: List<Track>) : FavoriteTracksState()
}