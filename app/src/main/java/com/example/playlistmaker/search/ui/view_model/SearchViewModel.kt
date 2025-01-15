package com.example.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.TracksSearchInteractor
import com.example.playlistmaker.search.domain.model.Track

class SearchViewModel(
    private val tracksHistoryInteractor: TrackHistoryInteractor,
    private val tracksSearchInteractor: TracksSearchInteractor
) : ViewModel() {

    private val trackListState = MutableLiveData<TrackListState>()

    fun getTrackListState(): LiveData<TrackListState> = trackListState

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private var searchText = ""

    private val searchRunnable = Runnable { search(searchText) }

    fun search(queryText: String) {
        if (queryText.isNotEmpty()) {
            trackListState.postValue(TrackListState.Loading)

            tracksSearchInteractor.searchTracks(
                queryText,
                object : TracksSearchInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?) {
                        if (foundTracks == null) {
                            trackListState.postValue(TrackListState.Error(ErrorSearchStatus.NO_INTERNET))
                        } else if (foundTracks.isNotEmpty()) {
                            trackListState.postValue(TrackListState.Content(foundTracks))
                        } else {
                            trackListState.postValue(TrackListState.Error(ErrorSearchStatus.NOT_FOUND))
                        }
                    }
                }
            )
        }
    }

    fun saveTrack(track: Track) {
        tracksHistoryInteractor.saveTrack(track)
    }

    fun clearHistory() {
        tracksHistoryInteractor.clear()
    }

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    fun onTextChange(queryText: String) {
        if (searchText == queryText) return

        searchText = queryText

        if (queryText.isEmpty()) {
            handler.removeCallbacks(searchRunnable)
            showHistory()
        } else {
            searchDebounce()
        }
    }

    private fun showHistory() {
        trackListState.postValue(TrackListState.History(tracksHistoryInteractor.getTracks()))
    }

    fun queryInputOnFocused() {
        showHistory()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L

//        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val tracksHistoryInteractor = Creator.provideTracksHistoryInteractor()
//                val tracksSearchInteractor = Creator.provideTracksSearchInteractor()
//
//                SearchViewModel(
//                    tracksHistoryInteractor,
//                    tracksSearchInteractor,
//                )
//            }
//        }
    }
}