package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.repository.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TrackHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TracksSearchRepositoryImpl
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.MediaPlayerRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.api.TrackHistoryInteractor
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.api.TracksSearchInteractor
import com.example.playlistmaker.domain.api.TracksSearchRepository
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.impl.TrackHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.TracksSearchInteractorImpl

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getTracksSearchRepository(): TracksSearchRepository {
        return TracksSearchRepositoryImpl(RetrofitClient(application))
    }

    fun provideTracksSearchInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(getTracksSearchRepository())
    }

    private fun getTrackHistoryRepository(): TrackHistoryRepository {
        return TrackHistoryRepositoryImpl(application)
    }

    fun provideTracksHistoryInteractor(): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getTrackHistoryRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(provideSharedPreferences())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getMediaPlayerRepository(mediaPlayer: MediaPlayer): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(mediaPlayer)
    }

    fun provideMediaPlayerInteractor(mediaPlayer: MediaPlayer): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository(mediaPlayer))
    }




}