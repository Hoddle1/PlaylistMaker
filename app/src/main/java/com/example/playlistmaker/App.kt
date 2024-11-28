package com.example.playlistmaker

import android.app.Application

class App : Application() {

    private val settingsInteractor by lazy { Creator.provideSettingsInteractor() }

    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        settingsInteractor.switchTheme(settingsInteractor.getTheme())
    }

}