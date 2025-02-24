package com.example.playlistmaker.data.search.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class TrackSearchDto(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int,
    val artworkUrl100: String,
    var collectionName: String?,
    val releaseDate: Date?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackSearchDto

        return trackId == other.trackId
    }

    override fun hashCode(): Int {
        return trackId
    }
}