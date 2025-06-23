package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.search.TrackHistoryInteractor
import com.example.playlistmaker.domain.search.TracksSearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksHistoryInteractor: TrackHistoryInteractor,
    private val tracksSearchInteractor: TracksSearchInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private val trackListState = MutableLiveData<TrackListState>()
    private var searchText = ""
    private var searchDebounceJob: Job? = null

    fun getTrackListState(): LiveData<TrackListState> = trackListState

    init {
        observeFavoriteUpdates()
    }

    private fun observeFavoriteUpdates() {
        viewModelScope.launch {
            favoriteTrackInteractor.favoritesUpdates.collect {
                if (trackListState.value is TrackListState.Content) {
                    search(searchText)
                }
                else if (trackListState.value is TrackListState.History) {
                    showHistory()
                }
            }
        }
    }

    fun search(queryText: String) {
        if (queryText.isNotEmpty()) {
            trackListState.postValue(TrackListState.Loading)

            viewModelScope.launch {
                tracksSearchInteractor
                    .searchTracks(queryText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundNames: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundNames != null) {
            tracks.addAll(foundNames)
        }

        if (errorMessage != null) {
            trackListState.postValue(TrackListState.Error(ErrorSearchStatus.NO_INTERNET))
        } else if (tracks.isEmpty()) {
            trackListState.postValue(TrackListState.Error(ErrorSearchStatus.NOT_FOUND))
        } else {
            trackListState.postValue(TrackListState.Content(tracks))
        }
    }

    fun saveTrack(track: Track) {
        tracksHistoryInteractor.saveTrack(track)
    }

    fun clearHistory() {
        tracksHistoryInteractor.clear()
    }

    fun onTextChange(queryText: String) {
        if (searchText == queryText) return

        searchText = queryText

        if (queryText.isEmpty()) {
            searchDebounceJob?.cancel()
            showHistory()
        } else {
            searchDebounceJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
                search(searchText)
            }
        }
    }

    private fun showHistory() {
        viewModelScope.launch {
            trackListState.postValue(TrackListState.History(tracksHistoryInteractor.getTracks()))
        }

    }

    fun queryInputOnFocused() {
        showHistory()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}