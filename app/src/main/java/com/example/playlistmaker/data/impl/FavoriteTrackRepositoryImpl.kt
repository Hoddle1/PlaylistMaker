package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.convertor.FavoriteTrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.domain.db.FavoriteTrackRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favoriteTrackDbConvertor: FavoriteTrackDbConvertor,
) : FavoriteTrackRepository {
    override suspend fun addFavoriteTrack(track: Track) {
        appDatabase.favoriteTrackDao().insertTrack(favoriteTrackDbConvertor.map(track))
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        appDatabase.favoriteTrackDao().deleteTrack(favoriteTrackDbConvertor.map(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favoriteTrackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map { track -> favoriteTrackDbConvertor.map(track) }
    }

}