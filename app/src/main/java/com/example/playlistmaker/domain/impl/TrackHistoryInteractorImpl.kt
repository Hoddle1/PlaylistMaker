package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.search.TrackHistoryInteractor
import com.example.playlistmaker.domain.search.TrackHistoryRepository

class TrackHistoryInteractorImpl(
    private val repository: TrackHistoryRepository,
    private val appDatabase: AppDatabase
) : TrackHistoryInteractor {
    override fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    override suspend fun getTracks(): List<Track> {
        val tracks = repository.getTracks()
        val favoriteTrackIds = appDatabase.favoriteTrackDao().getTrackIds()
        tracks.forEach { track ->
            track.isFavorite = track.trackId in favoriteTrackIds
        }
        return tracks
    }

    override fun clear() {
        repository.clear()
    }
}