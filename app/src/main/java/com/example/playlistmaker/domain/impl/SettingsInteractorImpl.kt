package com.example.playlistmaker.domain.impl


import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.model.ThemeSettings

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