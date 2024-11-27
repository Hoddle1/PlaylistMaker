package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.model.ThemeSettingsDto
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getTheme(): ThemeSettingsDto {
        return repository.getTheme()
    }

    override fun saveTheme(theme: ThemeSettingsDto) {
        repository.saveTheme(theme)
    }

    override fun switchTheme(theme: ThemeSettingsDto) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme.darkMode) {
                null -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                true -> AppCompatDelegate.MODE_NIGHT_YES
                false -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        saveTheme(theme)
    }
}