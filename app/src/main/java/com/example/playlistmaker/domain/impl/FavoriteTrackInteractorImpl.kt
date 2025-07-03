package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.domain.db.FavoriteTrackRepository
import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FavoriteTrackInteractorImpl(private val repository: FavoriteTrackRepository) :
    FavoriteTrackInteractor {
    private val updates = MutableSharedFlow<Unit>()
    override val favoritesUpdates: SharedFlow<Unit> = updates.asSharedFlow()

    override suspend fun addFavoriteTrack(track: Track) {
        repository.addFavoriteTrack(track)
        updates.emit(Unit)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        repository.deleteFavoriteTrack(track)
        updates.emit(Unit)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }
}