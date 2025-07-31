package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist): Boolean
    suspend fun deletePlaylist(playlistId: Int)

    suspend fun insertTrackToPlaylist(playlist: Playlist, track: Track): Boolean
    suspend fun removeTrackFromPlaylist(playlist: Playlist, track: Track): Playlist?

    fun getTracksForPlaylist(trackIds: List<Int>): Flow<List<Track>>
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistById(id: Int): Playlist
}