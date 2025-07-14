package com.example.playlistmaker.presentation.ui.playlist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.playlistmaker.domain.entity.Track

class TracksDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}