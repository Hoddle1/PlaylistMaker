package com.example.playlistmaker.domain.entity

data class Playlist(
    val id: Int,
    val name: String,
    val description: String?,
    val coverImagePath: String?,
    val trackIds: List<Int>,
    val tracksCount: Int,
)
