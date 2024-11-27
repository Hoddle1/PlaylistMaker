package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.model.ThemeSettingsDto

interface SettingsRepository {
    fun getTheme(): ThemeSettingsDto
    fun saveTheme(theme: ThemeSettingsDto)
}