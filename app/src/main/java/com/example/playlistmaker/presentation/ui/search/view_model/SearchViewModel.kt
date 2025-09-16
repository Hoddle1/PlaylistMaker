package com.example.playlistmaker.presentation.ui.search.view_model

import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksHistoryInteractor: TrackHistoryInteractor,
    private val tracksSearchInteractor: TracksSearchInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private val trackListState = MutableStateFlow<TrackListState>(TrackListState.Loading)
    fun getTrackListState(): StateFlow<TrackListState> = trackListState

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private var searchDebounceJob: Job? = null


    init {
        observeFavoriteUpdates()
    }

    private fun observeFavoriteUpdates() {
        viewModelScope.launch {
            favoriteTrackInteractor.favoritesUpdates.collect {
                if (trackListState.value is TrackListState.Content) {
                    search(_searchText.value)
                } else if (trackListState.value is TrackListState.History) {
                    showHistory()
                }
            }
        }
    }

    fun search(queryText: String) {
        if (queryText.isNotEmpty()) {
            trackListState.value = TrackListState.Loading

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
            Log.i("text", tracks.toString())
            tracks.addAll(foundNames)
        }

        if (errorMessage != null) {
            trackListState.value = TrackListState.Error(ErrorSearchStatus.NO_INTERNET)
        } else if (tracks.isEmpty()) {
            trackListState.value = TrackListState.Error(ErrorSearchStatus.NOT_FOUND)
        } else {
            trackListState.value = TrackListState.Content(tracks)
        }
    }

    fun saveTrack(track: Track) {
        tracksHistoryInteractor.saveTrack(track)
    }

    fun clearHistory() {
        tracksHistoryInteractor.clear()
    }

    fun clearSearch() {
        _searchText.value = ""
    }

    fun onTextChange(queryText: String) {
        if (_searchText.value == queryText) return

        _searchText.value = queryText

        searchDebounceJob?.cancel()

        if (queryText.isEmpty()) {
            searchDebounceJob?.cancel()
            showHistory()
        } else {
            searchDebounceJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
                search(_searchText.value)
            }
        }
    }

    private fun showHistory() {
        viewModelScope.launch {
            trackListState.value = TrackListState.History(tracksHistoryInteractor.getTracks())
        }

    }

    fun queryInputOnFocused() {
        showHistory()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}