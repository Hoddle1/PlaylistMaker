package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {


    private val isDarkTheme = MutableLiveData<Boolean>(settingsInteractor.getTheme().darkMode)

    fun observeDarkTheme(): LiveData<Boolean> = isDarkTheme

    fun switchTheme(darkMode: Boolean) {
        isDarkTheme.postValue(darkMode)

        settingsInteractor.switchTheme(
            ThemeSettings(
                darkMode = darkMode
            )
        )
    }

}