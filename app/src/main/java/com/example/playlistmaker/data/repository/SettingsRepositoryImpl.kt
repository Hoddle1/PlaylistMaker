package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.Constants.THEME_KEY
import com.example.playlistmaker.data.model.ThemeSettingsDto
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {

    override fun getTheme(): ThemeSettingsDto {
        val darkModeValue = sharedPreferences.contains(THEME_KEY)
            .takeIf { it }?.let { ThemeSettingsDto(sharedPreferences.getBoolean(THEME_KEY, false)) }
            ?: return ThemeSettingsDto(darkMode = null)

        return darkModeValue
    }

    override fun saveTheme(theme: ThemeSettingsDto) {
        theme.darkMode?.let {
            sharedPreferences.edit()
                .putBoolean(THEME_KEY, it)
                .apply()
        }
    }
}