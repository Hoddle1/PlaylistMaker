package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getTheme(): ThemeSettings
    fun saveTheme(theme: ThemeSettings)
    fun switchTheme(theme: ThemeSettings)
}