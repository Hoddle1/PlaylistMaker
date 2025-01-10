package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.example.playlistmaker.player.data.MediaPlayerRepository
import com.example.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.search.data.TrackHistoryRepository
import com.example.playlistmaker.search.data.TracksSearchRepository
import com.example.playlistmaker.search.data.impl.TrackHistoryRepositoryImpl
import com.example.playlistmaker.search.data.impl.TracksSearchRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.domain.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.TracksSearchInteractor
import com.example.playlistmaker.search.domain.impl.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksSearchInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
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