package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.convertor.FavoriteTrackDbConvertor
import com.example.playlistmaker.data.convertor.PlaylistDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.impl.FavoriteTrackRepositoryImpl
import com.example.playlistmaker.data.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.data.impl.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.player.impl.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.playlistadd.impl.PlaylistImageStorageRepositoryImpl
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.impl.TracksSearchRepositoryImpl
import com.example.playlistmaker.data.search.network.RetrofitClient
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.db.FavoriteTrackRepository
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.player.MediaPlayerRepository
import com.example.playlistmaker.domain.playlistadd.PlaylistImageStorageRepository
import com.example.playlistmaker.domain.search.TrackHistoryRepository
import com.example.playlistmaker.domain.search.TracksSearchRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

val dataModule = module {

    factory { MediaPlayer() }

    factory { FavoriteTrackDbConvertor() }

    factory { Gson() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single {
        androidContext().getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitClient(
            context = androidContext()
        )
    }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(
            favoriteTrackDbConvertor = get(),
            appDatabase = get()
        )
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            mediaPlayer = get()
        )
    }


    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }

    single<TracksSearchRepository> {
        TracksSearchRepositoryImpl(
            networkClient = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            sharedPreferences = get()
        )
    }

    factory {
        PlaylistDbConverter(
            gson = get()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDbConverter = get(),
            appDatabase = get()
        )
    }

    single<PlaylistImageStorageRepository> {
        PlaylistImageStorageRepositoryImpl(
           context = get()
        )
    }
}