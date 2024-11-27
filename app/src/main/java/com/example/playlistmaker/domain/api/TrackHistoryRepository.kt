package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TrackHistoryRepository {
    fun saveTrack(track: Track)
    fun getTracks(): List<Track>
    fun clear()
}