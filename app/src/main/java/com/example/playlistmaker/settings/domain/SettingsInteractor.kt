package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getTheme(): ThemeSettings
    fun saveTheme(theme: ThemeSettings)
    fun switchTheme(theme: ThemeSettings)
}