package com.example.playlistmaker.di

import com.example.playlistmaker.ui.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.library.view_model.PlayListViewModel
import com.example.playlistmaker.ui.player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.ui.playlistadd.view_model.AddPlaylistViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.util.UiMessageHelper
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
        PlayListViewModel(
            playlistInteractor = get()
        )
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

    viewModel {
        AddPlaylistViewModel(
            playlistInteractor = get()
        )
    }

    single {
        UiMessageHelper(
            context = get()
        )
    }


}