package com.example.playlistmaker.presentation.ui.library.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.components.Header
import com.example.playlistmaker.presentation.ui.theme.Fonts
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    onStartPlayerActivity: (Track) -> Unit,
    onStartPlaylistDetailFragment: (Playlist) -> Unit,
    onStartNewPlaylistFragment: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 2 })

    val selectedTabIndex = pagerState.currentPage

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(
            text = stringResource(R.string.library)
        )
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .customTabIndicatorOffset(
                            currentTabPosition = tabPositions[selectedTabIndex],
                            indicatorWidth = 148.dp
                        )
                        .height(2.dp),
                    color = colorResource(R.color.tab_library_color)
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                selectedContentColor = colorResource(R.color.tab_library_color),
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = {
                    Text(
                        color = colorResource(R.color.tab_library_color),
                        fontWeight = FontWeight.Medium,
                        fontFamily = Fonts.YSDisplay,
                        fontSize = 14.sp,
                        letterSpacing = 0.sp,
                        text = stringResource(R.string.favorites_tracks)
                    )
                },
            )

            Tab(
                selected = selectedTabIndex == 1,
                selectedContentColor = colorResource(R.color.tab_library_color),
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = {
                    Text(
                        color = colorResource(R.color.tab_library_color),
                        fontWeight = FontWeight.Medium,
                        fontFamily = Fonts.YSDisplay,
                        fontSize = 14.sp,
                        letterSpacing = 0.sp,
                        text = stringResource(R.string.playlists)
                    )
                },
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> FavoriteTracksScreen(
                    onTrackClick = { track ->
                        onStartPlayerActivity(track)
                    }
                )

                1 -> PlaylistsLibraryScreen(
                    onPlaylistClick = { playlist ->
                        onStartPlaylistDetailFragment(playlist)
                    },
                    onNewPlaylistClick = onStartNewPlaylistFragment
                )
            }
        }
    }
}

fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    indicatorWidth: Dp
): Modifier = this
    .wrapContentSize(Alignment.BottomStart)
    .offset(x = currentTabPosition.left + (currentTabPosition.width - indicatorWidth) / 2)
    .width(indicatorWidth)