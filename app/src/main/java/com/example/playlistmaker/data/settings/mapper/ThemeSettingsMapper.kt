package com.example.playlistmaker.data.settings.mapper

import com.example.playlistmaker.data.settings.dto.ThemeSettingsDto
import com.example.playlistmaker.domain.settings.model.ThemeSettings

object ThemeSettingsMapper {
    fun map(theme: ThemeSettingsDto): ThemeSettings {
        return ThemeSettings(theme.darkMode)
    }
}