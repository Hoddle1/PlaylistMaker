package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.domain.search.TrackHistoryInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val tracksHistoryInteractor: TrackHistoryInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private var favoriteTracksState =
        MutableLiveData<FavoriteTracksState>(FavoriteTracksState.Empty())

    fun getFavoriteTracksState(): LiveData<FavoriteTracksState> = favoriteTracksState

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
                        favoriteTracksState.postValue(FavoriteTracksState.Empty())
                    } else {
                        favoriteTracksState.postValue(FavoriteTracksState.Content(tracks))
                    }
                }
        }
    }

    fun saveTrackOnHistory(track: Track) {
        tracksHistoryInteractor.saveTrack(track)
    }

}