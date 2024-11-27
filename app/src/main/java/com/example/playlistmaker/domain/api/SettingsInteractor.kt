package com.example.playlistmaker.domain.api

import com.example.playlistmaker.data.model.ThemeSettingsDto

interface SettingsInteractor {
    fun getTheme(): ThemeSettingsDto
    fun saveTheme(theme: ThemeSettingsDto)
    fun switchTheme(theme: ThemeSettingsDto)
}