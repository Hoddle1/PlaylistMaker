package com.example.playlistmaker.settings.data.mapper

import com.example.playlistmaker.settings.data.dto.ThemeSettingsDto
import com.example.playlistmaker.settings.domain.model.ThemeSettings

object ThemeSettingsMapper {
    fun map(theme: ThemeSettingsDto): ThemeSettings {
        return ThemeSettings(theme.darkMode)
    }
}