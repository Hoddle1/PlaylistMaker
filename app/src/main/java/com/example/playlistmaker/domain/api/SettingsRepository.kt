package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.ThemeSettings

interface SettingsRepository {
    fun getTheme(): ThemeSettings
    fun saveTheme(theme: ThemeSettings)
    fun switchTheme(theme: ThemeSettings)
}