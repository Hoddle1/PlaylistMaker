package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.db.PlaylistInteractor

class PlayListViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var playlistState = MutableLiveData<PlaylistState>(PlaylistState.Empty())

    fun getFavoriteTracksState(): LiveData<PlaylistState> = playlistState



}