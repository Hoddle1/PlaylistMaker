package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Constants.THEME_KEY
import com.example.playlistmaker.data.mapper.ThemeSettingsMapper
import com.example.playlistmaker.data.model.ThemeSettingsDto
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.model.ThemeSettings

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {

    override fun getTheme(): ThemeSettings {
        val darkModeValue = sharedPreferences.contains(THEME_KEY)
            .takeIf { it }?.let { ThemeSettingsDto(sharedPreferences.getBoolean(THEME_KEY, false)) }
            ?: return ThemeSettingsMapper.map(ThemeSettingsDto(darkMode = null))

        return ThemeSettingsMapper.map(darkModeValue)
    }

    override fun saveTheme(theme: ThemeSettings) {
        theme.darkMode?.let {
            sharedPreferences.edit()
                .putBoolean(THEME_KEY, it)
                .apply()
        }
    }

    override fun switchTheme(theme: ThemeSettings) {
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