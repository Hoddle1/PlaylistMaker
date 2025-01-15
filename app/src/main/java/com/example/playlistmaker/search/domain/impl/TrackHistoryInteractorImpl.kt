package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.example.playlistmaker.search.domain.model.Track

class TrackHistoryInteractorImpl(
    private val repository: TrackHistoryRepository
) : TrackHistoryInteractor {
    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override fun getTracks(): List<Track> {
        return repository.getTracks()
    }

    override fun clear() {
        repository.clear()
    }
}