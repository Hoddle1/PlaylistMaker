package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksSearchInteractor {
    fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>>
}