package com.example.playlistmaker.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    val trackIds: List<Int>,
    val tracksCount: Int,
): Parcelable
