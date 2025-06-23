package com.example.playlistmaker.ui.library.view_model

import com.example.playlistmaker.domain.entity.Playlist

sealed class PlaylistState {
    class Empty : PlaylistState()
    class Content(val playlists: List<Playlist>) : PlaylistState()
}