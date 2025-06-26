package com.example.playlistmaker.domain.playlistadd.impl

import android.net.Uri
import com.example.playlistmaker.domain.playlistadd.PlaylistImageStorageInteractor
import com.example.playlistmaker.domain.playlistadd.PlaylistImageStorageRepository

class PlaylistImageStorageInteractorImpl(private val repository: PlaylistImageStorageRepository) :
    PlaylistImageStorageInteractor {
    override fun saveCoverImage(uri: Uri, name: String): Uri? {
        return repository.saveCoverImage(uri, name)
    }
}