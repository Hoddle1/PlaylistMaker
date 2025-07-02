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

    private var mediaPlayerState = MutableLiveData<MediaPlayerState>(
        MediaPlayerState.Default()
    )

    fun getMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    private val bottomSheetState = MutableLiveData<BottomSheetState>(BottomSheetState.Hidden)
    fun getBottomSheetState(): LiveData<BottomSheetState> = bottomSheetState

    private val uiMessage = MutableLiveData<String>()
    fun getUiMessageState(): LiveData<String> = uiMessage

    private var isFavorite = MutableLiveData<Boolean>()
    fun getIsFavorite(): LiveData<Boolean> = isFavorite

    private var timerJob: Job? = null

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

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(
            url = url,
            onPrependListener = {
                mediaPlayerState.postValue(MediaPlayerState.Prepared())
            },
            onCompletionListener = {
                timerJob?.cancel()
                mediaPlayerInteractor.seekTo(0)
                mediaPlayerState.postValue(MediaPlayerState.Prepared())
            }
        )
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

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        mediaPlayerState.value = MediaPlayerState.Playing(mediaPlayerInteractor.getPlayerTime())
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        timerJob?.cancel()
        mediaPlayerState.postValue(MediaPlayerState.Paused(mediaPlayerInteractor.getPlayerTime()))
    }

    private fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
        mediaPlayerState.value = MediaPlayerState.Default()
    }

    fun playbackControl() {
        when (mediaPlayerState.value) {
            is MediaPlayerState.Paused, is MediaPlayerState.Prepared -> {
                startPlayer()
            }

            is MediaPlayerState.Playing -> {
                pausePlayer()
            }

            else -> {}
        }
    }

    fun getPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collect { playlists ->
                    bottomSheetState.postValue(
                        BottomSheetState.Collapsed(
                            playlists = playlists
                        )
                    )
                }
        }
    }

    fun hideBottomSheet() {
        bottomSheetState.postValue(BottomSheetState.Hidden)
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayerState.value is MediaPlayerState.Playing) {
                delay(DELAY_MILLIS)
                mediaPlayerState.postValue(MediaPlayerState.Playing(mediaPlayerInteractor.getPlayerTime()))
            }
        }
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
            val result = playlistInteractor.updatePlaylist(playlist, track)

            if (result) {
                uiMessage.postValue(
                    uiTextProvider.getString(
                        R.string.track_added_in_playlist,
                        playlist.name
                    )
                )
            } else {
                uiMessage.postValue(
                    uiTextProvider.getString(
                        R.string.unknown_error
                    )
                )
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 500L
    }
}