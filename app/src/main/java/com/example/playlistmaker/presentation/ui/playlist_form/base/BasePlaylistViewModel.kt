package com.example.playlistmaker.presentation.ui.playlist_form.base

import android.net.Uri
import androidx.lifecycle.ViewModel

abstract class BasePlaylistViewModel: ViewModel() {
    abstract fun savePlaylist(
        name: String,
        description: String?,
        coverImagePath: String?
    )

    abstract fun saveCoverImage(uri: Uri, name: String): Uri?
}