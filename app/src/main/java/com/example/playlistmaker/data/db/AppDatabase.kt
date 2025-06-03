package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.data.db.dao.FavoriteTrackDao
import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.util.Converters

@Database(version = 2, entities = [FavoriteTrackEntity::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

}