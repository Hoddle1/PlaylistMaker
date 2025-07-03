package com.example.playlistmaker.presentation.util

import android.content.Context
import androidx.annotation.StringRes

class UiTextProviderImpl(private val context: Context) : UiTextProvider {
    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}