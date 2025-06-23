package com.example.playlistmaker.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.playlistmaker.R
import com.google.android.material.snackbar.Snackbar

class UiMessageHelper(private val context: Context) {
    fun showCustomSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
            .setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT))
        snackbar.view.background = ColorDrawable(Color.TRANSPARENT)

        val customView = LayoutInflater.from(context)
            .inflate(R.layout.toast_playlist_added, null)

        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .visibility = View.GONE

        customView.findViewById<TextView>(R.id.tvMessage).text = message
        (snackbar.view as ViewGroup).addView(customView, 0)
        snackbar.show()
    }
}