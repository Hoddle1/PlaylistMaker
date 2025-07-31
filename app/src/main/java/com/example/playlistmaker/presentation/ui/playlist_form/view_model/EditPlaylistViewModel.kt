package com.example.playlistmaker.presentation.ui.playlist_form.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.playlistadd.PlaylistImageStorageInteractor
import com.example.playlistmaker.presentation.ui.playlist_form.base.BasePlaylistViewModel
import kotlinx.coroutines.launch

class EditPlaylistViewModel (
    private val playlistInteractor: PlaylistInteractor,
    private val playlistImageStorageInteractor: PlaylistImageStorageInteractor
) : BasePlaylistViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    fun loadPlaylist(playlist: Playlist) {
        _playlist.value = playlist
    }

    override fun savePlaylist(name: String, description: String?, coverImagePath: String?) {
        val current = _playlist.value ?: return

        val updatedPlaylist = current.copy(
            name = name,
            description = description,
            coverImagePath = coverImagePath
        )

        viewModelScope.launch {
            playlistInteractor.updatePlaylist(updatedPlaylist)
        }
    }

    override fun saveCoverImage(uri: Uri, name: String): Uri? {
        return playlistImageStorageInteractor.saveCoverImage(uri, name)
    }
}