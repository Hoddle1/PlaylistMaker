package com.example.playlistmaker.domain.playlistadd

import android.net.Uri

interface PlaylistImageStorageRepository {
    fun saveCoverImage(uri: Uri, name: String): Uri?
}