package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "playlist_tracks_entity")
data class PlaylistTrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: Int,
    @ColumnInfo(name = "track_name")
    val trackName: String,
    @ColumnInfo(name = "artist_name")
    val artistName: String,
    @ColumnInfo(name = "track_time_millis")
    val trackTimeMillis: String,
    @ColumnInfo(name = "artwork_url_100")
    val artworkUrl100: String,
    @ColumnInfo(name = "collection_name")
    var collectionName: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: Date?,
    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean,
    @ColumnInfo(name = "preview_url")
    val previewUrl: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)