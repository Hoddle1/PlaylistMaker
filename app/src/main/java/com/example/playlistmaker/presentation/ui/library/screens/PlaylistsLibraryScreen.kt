package com.example.playlistmaker.presentation.ui.library.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.presentation.ui.components.NotFoundView
import com.example.playlistmaker.presentation.ui.components.RoundedButton
import com.example.playlistmaker.presentation.ui.library.components.PlaylistList
import com.example.playlistmaker.presentation.ui.library.view_model.PlaylistState
import com.example.playlistmaker.presentation.ui.library.view_model.PlaylistsLibraryViewModel
import com.example.playlistmaker.presentation.util.CompanionClass.Companion.CLICK_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.presentation.util.Utils.rememberDebounce
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsLibraryScreen(
    viewModel: PlaylistsLibraryViewModel = koinViewModel(),
    onPlaylistClick: (Playlist) -> Unit,
    onNewPlaylistClick: () -> Unit
) {

    val state by viewModel.getPlaylistsState().collectAsState()

    val onDebouncedClick = rememberDebounce<Playlist>(
        CLICK_DEBOUNCE_DELAY_MILLIS,
        useLastParam = false
    ) { playlist ->
        onPlaylistClick(playlist)
    }

    LaunchedEffect(Unit) {
        viewModel.getPlaylists()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {

        RoundedButton(
            text = stringResource(R.string.new_playlist),
            onClick = onNewPlaylistClick
        )

        when (val s = state) {
            is PlaylistState.Content -> {
                Spacer(Modifier.height(16.dp))
                PlaylistList(
                    playlists = s.playlists,
                    onPlaylistClick = { playlist ->
                        onDebouncedClick(playlist)
                    }
                )
            }

            is PlaylistState.Empty -> {
                Spacer(Modifier.height(46.dp))
                NotFoundView(
                    text = stringResource(R.string.no_playlists)
                )
            }
        }
    }
}