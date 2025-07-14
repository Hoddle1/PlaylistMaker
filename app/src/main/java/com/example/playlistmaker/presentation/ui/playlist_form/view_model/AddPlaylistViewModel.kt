package com.example.playlistmaker.presentation.ui.playlist_form.view_model

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.playlistadd.PlaylistImageStorageInteractor
import com.example.playlistmaker.presentation.ui.playlist_form.base.BasePlaylistViewModel
import kotlinx.coroutines.launch

class AddPlaylistViewModel (
    private val playlistInteractor: PlaylistInteractor,
    private val playlistImageStorageInteractor: PlaylistImageStorageInteractor
) : BasePlaylistViewModel() {

    override fun savePlaylist(name: String, description: String?, coverImagePath: String?) {
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

    override fun saveCoverImage(uri: Uri, name: String): Uri? {
        return playlistImageStorageInteractor.saveCoverImage(uri, name)
    }

}