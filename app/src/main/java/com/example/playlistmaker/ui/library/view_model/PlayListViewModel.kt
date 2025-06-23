package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class PlayListViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlistState = MutableLiveData<PlaylistState>(PlaylistState.Empty())

    fun getPlaylistsState(): LiveData<PlaylistState> = playlistState

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    playlistState.postValue(PlaylistState.Empty())
                } else {
                    playlistState.postValue(PlaylistState.Content(playlists))
                }
            }
        }
    }

}