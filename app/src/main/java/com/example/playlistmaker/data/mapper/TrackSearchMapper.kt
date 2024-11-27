package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.Utils
import com.example.playlistmaker.data.model.TrackSearchDto
import com.example.playlistmaker.domain.model.Track

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