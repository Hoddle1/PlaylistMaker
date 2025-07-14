package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {

    private val updates = MutableSharedFlow<Unit>()
    override val playlistsUpdates: SharedFlow<Unit> = updates.asSharedFlow()

    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
        updates.emit(Unit)
    }

    override suspend fun updatePlaylist(playlist: Playlist): Boolean {
        val result = repository.updatePlaylist(playlist)
        updates.emit(Unit)
        return result
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(playlistId)
        updates.emit(Unit)
    }

    override suspend fun insertTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        val updatedTrackIds = playlist.trackIds.toMutableList()
        updatedTrackIds.add(track.trackId)

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )

        val result = repository.insertTrackToPlaylist(updatedPlaylist, track)
        updates.emit(Unit)
        return result
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, track: Track): Playlist? {
        val playlist = repository.getPlaylistById(playlistId)

        val updatedTrackIds = playlist.trackIds.toMutableList().apply {
            remove(track.trackId)
        }

        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )

        val result = repository.removeTrackFromPlaylist(updatedPlaylist, track)
        updates.emit(Unit)
        return result
    }

    override fun getTracksForPlaylist(trackIds: List<Int>): Flow<List<Track>> {
        return repository.getTracksForPlaylist(trackIds)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return repository.getPlaylistById(id)
    }

}