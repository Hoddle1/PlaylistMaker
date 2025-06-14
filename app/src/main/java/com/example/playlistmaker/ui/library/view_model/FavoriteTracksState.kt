package com.example.playlistmaker.ui.library.view_model

import com.example.playlistmaker.domain.search.model.Track


sealed class FavoriteTracksState {
    class Empty : FavoriteTracksState()
    class Content(val tracks: List<Track>) : FavoriteTracksState()
}