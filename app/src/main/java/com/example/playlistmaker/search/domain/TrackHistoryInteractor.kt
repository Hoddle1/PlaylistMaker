package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface TrackHistoryInteractor {
    fun saveTrack(track: Track)
    fun getTracks(): List<Track>
    fun clear()
}