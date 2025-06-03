package com.example.playlistmaker.di

import com.example.playlistmaker.ui.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.library.view_model.PlayListViewModel
import com.example.playlistmaker.ui.player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    viewModel {
        FavoriteTracksViewModel(
            favoriteTrackInteractor = get(),
            tracksHistoryInteractor = get()
        )
    }

    viewModel {
        PlayListViewModel()
    }

    viewModel {
        MediaPlayerViewModel(
            mediaPlayerInteractor = get(),
            favoriteTrackInteractor = get()
        )
    }

    viewModel {
        SearchViewModel(
            tracksSearchInteractor = get(),
            tracksHistoryInteractor = get(),
            favoriteTrackInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get()
        )
    }
}