package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.entity.Track

interface TrackHistoryInteractor {
    fun saveTrack(track: Track)
    suspend fun getTracks(): List<Track>
    fun clear()
}