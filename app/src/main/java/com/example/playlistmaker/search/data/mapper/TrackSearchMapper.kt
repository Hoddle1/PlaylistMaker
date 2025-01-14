package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.search.data.dto.TrackSearchDto
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Utils

object TrackSearchMapper {
    fun map(tracks: List<TrackSearchDto>): List<Track> {
        return tracks.map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                Utils.convertMillisToTime(it.trackTimeMillis),
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }
}