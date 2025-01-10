package com.example.playlistmaker.settings.domain.impl


import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings

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