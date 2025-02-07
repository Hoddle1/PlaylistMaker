package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TracksSearchInteractor
import com.example.playlistmaker.search.domain.TracksSearchRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksSearchInteractorImpl(
    private val repository: TracksSearchRepository
) : TracksSearchInteractor {

    override fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(term).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}