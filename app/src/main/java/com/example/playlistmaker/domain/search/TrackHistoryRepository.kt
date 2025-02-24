package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TrackHistoryRepository {
    fun saveTrack(track: Track)
    fun getTracks(): List<Track>
    fun clear()
}