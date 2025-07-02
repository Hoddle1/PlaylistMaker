package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.presentation.ui.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.presentation.ui.library.view_model.PlayListViewModel
import com.example.playlistmaker.presentation.ui.player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.presentation.ui.playlistadd.view_model.AddPlaylistViewModel
import com.example.playlistmaker.presentation.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.presentation.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.presentation.util.UiMessageHelper
import com.example.playlistmaker.presentation.util.UiTextProvider
import com.example.playlistmaker.presentation.util.UiTextProviderImpl
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
            favoriteTrackInteractor = get(),
            playlistInteractor = get(),
            uiTextProvider = get()
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
            playlistInteractor = get(),
            playlistImageStorageInteractor = get()
        )
    }

    single<UiTextProvider> {
        UiTextProviderImpl(
            context = get()
        )
    }

    factory { (context: Context) ->
        UiMessageHelper(context)
    }

}