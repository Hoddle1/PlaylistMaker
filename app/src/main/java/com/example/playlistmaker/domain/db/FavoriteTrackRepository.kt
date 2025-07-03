package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
}