package com.example.playlistmaker.di

import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            sharedPreferences = get()
        )
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(
            repository = get()
        )
    }

    viewModel {
        SettingsViewModel(
            settingsInteractor = get()
        )
    }
}