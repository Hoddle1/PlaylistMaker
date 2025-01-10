package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.model.Track

interface TracksSearchRepository {
    fun searchTracks(term: String): List<Track>?
}