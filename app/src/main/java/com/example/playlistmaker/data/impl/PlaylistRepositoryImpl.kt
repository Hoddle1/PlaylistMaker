package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.convertor.PlaylistDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist): Boolean {
        return try {
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val playlist = getPlaylistById(playlistId)

        appDatabase.playlistDao().deletePlaylist(playlistDbConverter.map(playlist))

        playlist.trackIds.forEach { trackId ->
            if (isTrackUnused(trackId)) {
                appDatabase.playlistTrackDao().deleteTrackById(trackId)
            }
        }
    }

    override suspend fun insertTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        return try {
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
            appDatabase.playlistTrackDao().insertTrack(playlistDbConverter.map(track))
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, track: Track): Playlist? {
        return try {
            appDatabase.playlistDao().updatePlaylist(playlistDbConverter.map(playlist))
            if (isTrackUnused(track.trackId)) {
                appDatabase.playlistTrackDao().deleteTrackById(track.trackId)
            }
            playlist
        } catch (e: Exception) {
            null
        }
    }

    override fun getTracksForPlaylist(trackIds: List<Int>): Flow<List<Track>> = flow {
        val trackEntities = appDatabase.playlistTrackDao().getTracksForPlaylist(trackIds)
        val tracks = trackEntities.map { playlistDbConverter.map(it) }
        emit(tracks)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return playlistDbConverter.map(
            appDatabase.playlistDao().getPlaylistsById(id)
        )
    }

    private suspend fun isTrackUnused(trackId: Int): Boolean {
        val playlists = appDatabase.playlistDao().getPlaylists()

        return playlists.none {
            val playlist = playlistDbConverter.map(it)
            playlist.trackIds.contains(trackId)
        }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

}