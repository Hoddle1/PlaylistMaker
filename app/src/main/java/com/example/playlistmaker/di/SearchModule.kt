package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.impl.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.impl.TracksSearchRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.domain.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.TrackHistoryRepository
import com.example.playlistmaker.search.domain.TracksSearchInteractor
import com.example.playlistmaker.search.domain.TracksSearchRepository
import com.example.playlistmaker.search.domain.impl.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksSearchInteractorImpl
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

val searchModule = module {

    single {
        androidContext().getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<TrackHistoryRepository> {
        TrackHistoryRepositoryImpl(
            sharedPreferences = get(),
            gson = get()
        )
    }

    single<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(
            repository = get()
        )
    }

    single<NetworkClient> {
        RetrofitClient(
            context = androidContext()
        )
    }

    single<TracksSearchRepository> {
        TracksSearchRepositoryImpl(
            networkClient = get()
        )
    }

    single<TracksSearchInteractor> {
        TracksSearchInteractorImpl(
            repository = get()
        )
    }

    viewModel {
        SearchViewModel(
            tracksSearchInteractor = get(),
            tracksHistoryInteractor = get()
        )
    }
}
