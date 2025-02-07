package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksSearchRepository {
    fun searchTracks(term: String): Flow<Resource<List<Track>>>
}