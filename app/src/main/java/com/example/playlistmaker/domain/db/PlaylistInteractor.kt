package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface PlaylistInteractor {
    val playlistsUpdates: SharedFlow<Unit>

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist, track: Track): Boolean
    suspend fun deletePlaylist(playlist: Playlist)
    fun getPlaylists(): Flow<List<Playlist>>
}