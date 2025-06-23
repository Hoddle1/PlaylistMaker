package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface TracksSearchInteractor {
    fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>>
}