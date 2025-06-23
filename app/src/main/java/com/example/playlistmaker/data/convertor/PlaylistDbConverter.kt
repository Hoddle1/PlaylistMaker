package com.example.playlistmaker.data.convertor

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.entity.Playlist
import com.google.gson.Gson

class PlaylistDbConverter(
    private val gson: Gson
) {
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