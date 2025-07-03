package com.example.playlistmaker.presentation.util

import androidx.annotation.StringRes

interface UiTextProvider {
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}