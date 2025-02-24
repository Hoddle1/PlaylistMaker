package com.example.playlistmaker.domain.settings

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsInteractor {
    fun getTheme(): ThemeSettings
    fun saveTheme(theme: ThemeSettings)
    fun switchTheme(theme: ThemeSettings)
}