package com.example.playlistmaker.presentation.ui.playlist.entity

import com.example.playlistmaker.domain.entity.Track

data class PlaylistTracksState(
    val tracks: List<Track>,
    val totalDurationMinutes: Int
)