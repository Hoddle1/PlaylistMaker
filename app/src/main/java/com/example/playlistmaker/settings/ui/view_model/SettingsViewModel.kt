package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.model.ThemeSettings

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

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsInteractor = Creator.provideSettingsInteractor()

                SettingsViewModel(
                    settingsInteractor
                )
            }
        }
    }

}