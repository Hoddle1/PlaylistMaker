package com.example.playlistmaker.presentation.ui.library.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.components.NotFoundView
import com.example.playlistmaker.presentation.ui.components.TrackList
import com.example.playlistmaker.presentation.ui.library.view_model.FavoriteTracksState
import com.example.playlistmaker.presentation.ui.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.presentation.util.CompanionClass.Companion.CLICK_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.presentation.util.Utils.rememberDebounce
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoriteTracksScreen(
    viewModel: FavoriteTracksViewModel = koinViewModel(),
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.getFavoriteTracksState().collectAsState()

    val onDebouncedClick = rememberDebounce<Track>(
        CLICK_DEBOUNCE_DELAY_MILLIS,
        useLastParam = false
    ) { track ->
        viewModel.saveTrackOnHistory(track)
        onTrackClick(track)
    }

    LaunchedEffect(Unit) {
        viewModel.getFavoriteTracks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        when (val s = state) {
            is FavoriteTracksState.Content -> {
                Spacer(Modifier.height(16.dp))
                TrackList(
                    tracks = s.tracks,
                    onClick = { track ->
                        onDebouncedClick(track)
                    }
                )
            }

            is FavoriteTracksState.Empty ->{
                Spacer(Modifier.height(106.dp))
                NotFoundView(
                    text = stringResource(R.string.no_favorites)
                )
            }
        }
    }
}

