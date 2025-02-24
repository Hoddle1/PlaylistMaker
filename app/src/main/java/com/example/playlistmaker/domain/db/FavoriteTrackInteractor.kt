package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}