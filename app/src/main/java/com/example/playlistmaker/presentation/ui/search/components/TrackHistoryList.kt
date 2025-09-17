package com.example.playlistmaker.presentation.ui.search.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.components.RoundedButton
import com.example.playlistmaker.presentation.ui.components.TrackItem
import com.example.playlistmaker.presentation.ui.theme.Fonts

@Composable
fun TrackHistoryList(
    tracks: List<Track>,
    onTrackClickDebounce: (Track) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    Spacer(Modifier.height(42.dp))

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp, bottom = 12.dp)

    ) {
        Text(
            color = colorResource(R.color.history_text_color),
            text = stringResource(R.string.search_history),
            fontFamily = Fonts.YSDisplay,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp
        )
    }

    Spacer(Modifier.height(8.dp))

    LazyColumn {
        items(
            items = tracks,
            key = { it.trackId }
        ) { track ->
            TrackItem(
                track = track,
                onClick = { onTrackClickDebounce(track) }
            )
        }

        item { Spacer(Modifier.height(24.dp)) }

        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                RoundedButton(
                    text = stringResource(R.string.clear_history),
                    onClick = onClearHistoryClick
                )
            }

        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}