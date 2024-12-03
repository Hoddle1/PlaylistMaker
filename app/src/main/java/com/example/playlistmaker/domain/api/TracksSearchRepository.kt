package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TracksSearchRepository {
    fun searchTracks(term: String): List<Track>?
}