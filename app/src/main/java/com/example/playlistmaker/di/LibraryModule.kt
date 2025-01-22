package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.library.ui.view_model.PlayListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {

    viewModel {
        FavoriteTracksViewModel()
    }

    viewModel {
        PlayListViewModel()
    }
}