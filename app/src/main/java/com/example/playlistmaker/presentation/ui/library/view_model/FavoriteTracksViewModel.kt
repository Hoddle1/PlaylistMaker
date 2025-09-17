package com.example.playlistmaker.presentation.ui.library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.search.TrackHistoryInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val tracksHistoryInteractor: TrackHistoryInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private var favoriteTracksState =
        MutableStateFlow<FavoriteTracksState>(FavoriteTracksState.Empty())

    fun getFavoriteTracksState(): StateFlow<FavoriteTracksState> = favoriteTracksState

    init {
        observeFavoritesUpdates()
    }

    private fun observeFavoritesUpdates() {
        viewModelScope.launch {
            favoriteTrackInteractor.favoritesUpdates.collect {
                getFavoriteTracks()
            }
        }
    }

    fun getFavoriteTracks() {
        viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteTracks()
                .map { tracks ->
                    tracks.map { track ->
                        track.copy(isFavorite = true)
                    }
                }
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        favoriteTracksState.value = FavoriteTracksState.Empty()
                    } else {
                        favoriteTracksState.value = FavoriteTracksState.Content(tracks)
                    }
                }
        }
    }

    fun saveTrackOnHistory(track: Track) {
        tracksHistoryInteractor.saveTrack(track)
    }

}