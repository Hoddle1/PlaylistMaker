package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.domain.search.model.Track

sealed interface TrackListState {
    data object Loading : TrackListState
    data class Error(val status: ErrorSearchStatus) : TrackListState
    data class Content(val tracks: List<Track>) : TrackListState
    data class History(val tracks: List<Track>) : TrackListState
}