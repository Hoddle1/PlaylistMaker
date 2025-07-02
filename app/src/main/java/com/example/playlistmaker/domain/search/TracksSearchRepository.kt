package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksSearchRepository {
    fun searchTracks(term: String): Flow<Resource<List<Track>>>
}