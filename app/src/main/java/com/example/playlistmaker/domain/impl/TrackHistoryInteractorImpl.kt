package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.model.Track

class TrackHistoryInteractorImpl(private val repository: TrackHistoryRepository) :
    TrackHistoryInteractor {
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