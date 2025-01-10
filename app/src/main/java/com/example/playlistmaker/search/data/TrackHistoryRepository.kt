package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.model.Track

interface TrackHistoryRepository {
    fun saveTrack(track: Track)
    fun getTracks(): List<Track>
    fun clear()
}