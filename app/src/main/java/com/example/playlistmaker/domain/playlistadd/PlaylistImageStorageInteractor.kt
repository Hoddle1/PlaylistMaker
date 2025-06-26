package com.example.playlistmaker.domain.playlistadd

import android.net.Uri

interface PlaylistImageStorageInteractor {
    fun saveCoverImage(uri: Uri, name: String): Uri?
}