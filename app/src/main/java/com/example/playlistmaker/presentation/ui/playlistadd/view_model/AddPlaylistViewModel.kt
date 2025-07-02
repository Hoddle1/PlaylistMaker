package com.example.playlistmaker.presentation.ui.playlistadd.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.playlistadd.PlaylistImageStorageInteractor
import kotlinx.coroutines.launch

class AddPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val playlistImageStorageInteractor: PlaylistImageStorageInteractor
) : ViewModel() {

    fun createPlaylist(name: String, description: String?, coverImagePath: String?) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(
                playlist = Playlist(
                    id = 0,
                    name = name,
                    description = description,
                    coverImagePath = coverImagePath,
                    trackIds = emptyList(),
                    tracksCount = 0
                )
            )
        }
    }

    fun saveCoverImage(uri: Uri, name: String): Uri? {
        return playlistImageStorageInteractor.saveCoverImage(uri, name)
    }

}