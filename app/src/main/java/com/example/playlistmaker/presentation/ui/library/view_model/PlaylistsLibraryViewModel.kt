package com.example.playlistmaker.presentation.ui.library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistsLibraryViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlistState = MutableStateFlow<PlaylistState>(
        PlaylistState.Empty()
    )
    fun getPlaylistsState(): StateFlow<PlaylistState> = playlistState

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    playlistState.value = PlaylistState.Empty()
                } else {
                    playlistState.value = PlaylistState.Content(playlists)
                }
            }
        }
    }

}