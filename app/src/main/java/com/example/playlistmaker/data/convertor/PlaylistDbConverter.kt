package com.example.playlistmaker.data.convertor

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import com.google.gson.Gson

class PlaylistDbConverter(
    private val gson: Gson
) {

    fun map(playlistTrackEntity: PlaylistTrackEntity): Track {
        return Track(
            trackId = playlistTrackEntity.trackId,
            trackName = playlistTrackEntity.trackName,
            artistName = playlistTrackEntity.artistName,
            trackTimeMillis = playlistTrackEntity.trackTimeMillis,
            artworkUrl100 = playlistTrackEntity.artworkUrl100,
            collectionName = playlistTrackEntity.collectionName,
            releaseDate = playlistTrackEntity.releaseDate,
            primaryGenreName = playlistTrackEntity.primaryGenreName,
            country = playlistTrackEntity.country,
            previewUrl = playlistTrackEntity.previewUrl,
            isFavorite = playlistTrackEntity.isFavorite
        )
    }

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }


    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIds = listToString(playlist.trackIds),
            tracksCount = playlist.tracksCount,
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            coverImagePath = playlistEntity.coverImagePath,
            trackIds = stringToList(playlistEntity.trackIds),
            tracksCount = playlistEntity.tracksCount,
        )
    }

    private fun listToString(trackIds: List<Int>): String {
        return gson.toJson(trackIds)
    }

    private fun stringToList(trackIdsString: String): List<Int> {
        return gson.fromJson(trackIdsString, Array<Int>::class.java).toList()
    }
}