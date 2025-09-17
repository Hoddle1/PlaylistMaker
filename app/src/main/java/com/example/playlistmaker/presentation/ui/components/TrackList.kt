package com.example.playlistmaker.presentation.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.playlistmaker.domain.entity.Track

@Composable
fun TrackList(
    tracks: List<Track>,
    onClick: (Track) -> Unit
) {
    LazyColumn {
        items(
            items = tracks,
            key = { it.trackId }
        ) { track ->
            TrackItem(
                track = track,
                onClick = { onClick(track) }
            )
        }
    }
}