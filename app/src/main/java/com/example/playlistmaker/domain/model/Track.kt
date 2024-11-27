package com.example.playlistmaker.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    var collectionName: String?,
    val releaseDate: Date,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Track

        return trackId == other.trackId
    }

    override fun hashCode(): Int {
        return trackId
    }
}