package com.example.playlistmaker.ui.playlistadd.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.entity.Playlist
import kotlinx.coroutines.launch

class AddPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun createPlaylist(name: String, description: String?, coverImagePath: String?){
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

}