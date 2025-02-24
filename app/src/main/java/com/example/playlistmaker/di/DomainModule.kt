package com.example.playlistmaker.di

import com.example.playlistmaker.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.domain.impl.FavoriteTrackInteractorImpl
import com.example.playlistmaker.domain.impl.TrackHistoryInteractorImpl
import com.example.playlistmaker.domain.player.MediaPlayerInteractor
import com.example.playlistmaker.domain.player.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.search.TrackHistoryInteractor
import com.example.playlistmaker.domain.search.TracksSearchInteractor
import com.example.playlistmaker.domain.search.impl.TracksSearchInteractorImpl
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    single<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(
            repository = get()
        )
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(
            repository = get()
        )
    }

    single<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(
            repository = get(),
            appDatabase = get()
        )
    }

    single<TracksSearchInteractor> {
        TracksSearchInteractorImpl(
            repository = get(),
            appDatabase = get()
        )
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(
            repository = get()
        )
    }
}