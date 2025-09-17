package com.example.playlistmaker.presentation.ui.search.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.components.Header
import com.example.playlistmaker.presentation.ui.components.NotFoundView
import com.example.playlistmaker.presentation.ui.components.TrackList
import com.example.playlistmaker.presentation.ui.search.components.LoadingView
import com.example.playlistmaker.presentation.ui.search.components.NoInternetView
import com.example.playlistmaker.presentation.ui.search.components.SearchField
import com.example.playlistmaker.presentation.ui.search.components.TrackHistoryList
import com.example.playlistmaker.presentation.ui.search.view_model.ErrorSearchStatus
import com.example.playlistmaker.presentation.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.presentation.ui.search.view_model.TrackListState
import com.example.playlistmaker.presentation.util.CompanionClass.Companion.CLICK_DEBOUNCE_DELAY_MILLIS
import com.example.playlistmaker.presentation.util.Utils.rememberDebounce
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    startPlayerActivity: (Track) -> Unit
) {
    val searchText by viewModel.searchText.collectAsState()
    val state: TrackListState by viewModel.getTrackListState().collectAsState()
    val onDebouncedClick = rememberDebounce<Track>(
        CLICK_DEBOUNCE_DELAY_MILLIS,
        useLastParam = false
    ) { track ->
        viewModel.saveTrack(track)
        startPlayerActivity(track)
    }

    Scaffold(
        backgroundColor = colorResource(R.color.default_background)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {

            Header(
                text = stringResource(R.string.search)
            )

            SearchField(
                value = searchText,
                onValueChange = { text ->
                    viewModel.onTextChange(text)
                },
                onFocus = {
                    viewModel.queryInputOnFocused()
                },
                onClear = {
                    viewModel.clearSearch()
                }
            )

            when (val s = state) {
                is TrackListState.Content -> {
                    Spacer(Modifier.height(16.dp))
                    TrackList(
                        tracks = s.tracks,
                        onClick = { track ->
                            onDebouncedClick(track)
                        }
                    )
                }

                is TrackListState.Error -> {
                    Spacer(Modifier.height(102.dp))
                    when (s.status) {
                        ErrorSearchStatus.NOT_FOUND -> {

                            NotFoundView(
                                text = stringResource(R.string.not_found)
                            )
                        }

                        ErrorSearchStatus.NO_INTERNET -> {
                            NoInternetView(
                                onRetry = { viewModel.search(searchText) }
                            )
                        }
                    }
                }

                is TrackListState.History -> {
                    if (s.tracks.isNotEmpty()) {
                        TrackHistoryList(
                            tracks = s.tracks,
                            onTrackClickDebounce = { track -> onDebouncedClick(track) },
                            onClearHistoryClick = { viewModel.clearHistory() }
                        )
                    }
                }

                TrackListState.Loading -> {
                    LoadingView()
                }

                TrackListState.Empty -> {}
            }
        }
    }
}