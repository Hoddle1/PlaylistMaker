package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.presentation.ui.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.presentation.ui.library.view_model.PlaylistsLibraryViewModel
import com.example.playlistmaker.presentation.ui.player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.presentation.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.presentation.ui.playlist_form.view_model.AddPlaylistViewModel
import com.example.playlistmaker.presentation.ui.playlist_form.view_model.EditPlaylistViewModel
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
        PlaylistsLibraryViewModel(
            playlistInteractor = get()
        )
    }

    viewModel {
        MediaPlayerViewModel(
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

    viewModel {
        EditPlaylistViewModel(
            playlistInteractor = get(),
            playlistImageStorageInteractor = get()
        )
    }

    viewModel {
        PlaylistViewModel(
            playlistInteractor = get()
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