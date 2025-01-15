package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.MediaPlayerRepository
import com.example.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.player.ui.view_model.MediaPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory { MediaPlayer() }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            mediaPlayer = get()
        )
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(
            repository = get()
        )
    }

    viewModel {
        MediaPlayerViewModel(
            mediaPlayerInteractor = get()
        )
    }

}