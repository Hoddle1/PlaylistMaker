package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

class App : Application() {

    private var theme: Int = Constants.SYSTEM_THEME

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        theme = sharedPrefs.getInt("dark_theme", Constants.SYSTEM_THEME)
        switchTheme(theme)
    }

    fun switchTheme(theme: Int) {
        this.theme = theme

        val mode = when (theme) {
            Constants.SYSTEM_THEME -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            Constants.DARK_THEME -> AppCompatDelegate.MODE_NIGHT_YES
            Constants.LIGHT_THEME -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}