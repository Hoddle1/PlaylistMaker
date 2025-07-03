package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.search.TracksSearchInteractor
import com.example.playlistmaker.domain.search.TracksSearchRepository
import com.example.playlistmaker.presentation.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksSearchInteractorImpl(
    private val repository: TracksSearchRepository,
    private val appDatabase: AppDatabase
) : TracksSearchInteractor {

    override fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(term).map { result ->
            when (result) {
                is Resource.Success -> {
                    val favoriteTrackIds = appDatabase.favoriteTrackDao().getTrackIds()
                    result.data?.forEach { track ->
                        track.isFavorite = track.trackId in favoriteTrackIds
                    }
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}