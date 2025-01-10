package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsViewModel : ViewModel() {
    private val settingsInteractor = Creator.provideSettingsInteractor()

    private val isDarkTheme = MutableLiveData<Boolean>()

    fun observeDarkTheme(): LiveData<Boolean> = isDarkTheme

    init {
        isDarkTheme.postValue(settingsInteractor.getTheme().darkMode)
    }

    fun switchTheme(darkMode: Boolean) {
        isDarkTheme.postValue(darkMode)

        settingsInteractor.switchTheme(
            ThemeSettings(
                darkMode = darkMode
            )
        )
    }

}