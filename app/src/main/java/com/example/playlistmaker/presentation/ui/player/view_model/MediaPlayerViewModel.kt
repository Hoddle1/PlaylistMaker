package com.example.playlistmaker.presentation.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.domain.db.PlaylistInteractor
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.player.MediaPlayerInteractor
import com.example.playlistmaker.presentation.util.UiTextProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val uiTextProvider: UiTextProvider
) : ViewModel() {

    private var mediaPlayerState = MutableLiveData<MediaPlayerState>(MediaPlayerState.Default())
    fun getMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    private val bottomSheetState = MutableLiveData(BottomSheetState.HIDDEN)
    fun getBottomSheetState(): LiveData<BottomSheetState> = bottomSheetState

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun getPlaylistsLiveData(): LiveData<List<Playlist>> = playlistsLiveData

    private val uiMessage = MutableLiveData<String>()
    fun getUiMessageState(): LiveData<String> = uiMessage

    private var isFavorite = MutableLiveData<Boolean>()
    fun getIsFavorite(): LiveData<Boolean> = isFavorite

    private var audioPlayerControl: AudioPlayerControl? = null

    init {
        observePlaylistsUpdates()
    }

    private fun observePlaylistsUpdates() {
        viewModelScope.launch {
            playlistInteractor.playlistsUpdates.collect {
                getPlaylists()
            }
        }
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getPlayerState().collect {
                mediaPlayerState.postValue(it)
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }

    fun playbackControl() {
        when (mediaPlayerState.value) {
            is MediaPlayerState.Paused, is MediaPlayerState.Prepared -> {
                audioPlayerControl?.startPlayer()
            }

            is MediaPlayerState.Playing -> {
                audioPlayerControl?.pausePlayer()
            }

            else -> {}
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoriteTrackInteractor.deleteFavoriteTrack(track)
            } else {
                favoriteTrackInteractor.addFavoriteTrack(track)
            }
        }
        val newFavoriteState = !track.isFavorite
        track.isFavorite = newFavoriteState
        isFavorite.postValue(newFavoriteState)
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect { playlists ->
                playlistsLiveData.postValue(playlists)
            }
        }
    }

    fun showBottomSheet() {
        bottomSheetState.postValue(BottomSheetState.COLLAPSED)
    }

    fun hideBottomSheet() {
        bottomSheetState.postValue(BottomSheetState.HIDDEN)
    }

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {

        if (playlist.trackIds.contains(track.trackId)) {
            uiMessage.postValue(
                uiTextProvider.getString(
                    R.string.track_already_in_playlist,
                    playlist.name
                )
            )

            return
        }

        viewModelScope.launch {
            val result = playlistInteractor.insertTrackToPlaylist(playlist, track)
            if (result) {
                uiMessage.postValue(
                    uiTextProvider.getString(R.string.track_added_in_playlist, playlist.name)
                )
                hideBottomSheet()
            } else {
                uiMessage.postValue(uiTextProvider.getString(R.string.unknown_error))
            }
        }
    }
}