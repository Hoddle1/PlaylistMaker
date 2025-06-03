package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity

@Dao
interface FavoriteTrackDao {
    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(tracks: FavoriteTrackEntity)

    @Delete
    suspend fun deleteTrack(favoriteTrackEntity: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_track_table ORDER BY timestamp DESC")
    suspend fun getTracks(): List<FavoriteTrackEntity>

    @Query("SELECT track_id FROM favorite_track_table")
    suspend fun getTrackIds(): List<Int>


}