package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingsModule
import com.example.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(playerModule, searchModule, settingsModule)
        }

        val settingsInteractor: SettingsInteractor by inject()
        settingsInteractor.switchTheme(settingsInteractor.getTheme())
    }
}