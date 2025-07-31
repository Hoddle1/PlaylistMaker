package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {
    @Insert(entity = PlaylistTrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(playlistTrackEntity: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_entity WHERE track_id IN (:trackIds) ORDER BY timestamp DESC")
    suspend fun getTracksForPlaylist(trackIds: List<Int>): List<PlaylistTrackEntity>

    @Query("DELETE FROM playlist_tracks_entity WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: Int)
}