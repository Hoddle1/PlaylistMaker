package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TracksSearchInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}