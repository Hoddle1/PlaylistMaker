package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.model.ThemeSettingsDto
import com.example.playlistmaker.domain.model.ThemeSettings

object ThemeSettingsMapper {
    fun map(theme: ThemeSettingsDto): ThemeSettings {
        return ThemeSettings(theme.darkMode)
    }
}