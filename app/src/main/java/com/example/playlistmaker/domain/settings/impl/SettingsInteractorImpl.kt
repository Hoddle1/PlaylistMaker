package com.example.playlistmaker.domain.settings.impl


import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getTheme(): ThemeSettings {
        return repository.getTheme()
    }

    override fun saveTheme(theme: ThemeSettings) {
        repository.saveTheme(theme)
    }

    override fun switchTheme(theme: ThemeSettings) {
        repository.switchTheme(theme)
    }
}