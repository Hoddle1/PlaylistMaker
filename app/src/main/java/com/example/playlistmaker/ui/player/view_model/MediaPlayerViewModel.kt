package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteTrackInteractor
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.player.MediaPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private var mediaPlayerState = MutableLiveData<MediaPlayerState>(MediaPlayerState.Default())
    fun getMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    private var isFavorite = MutableLiveData<Boolean>()
    fun getIsFavorite(): LiveData<Boolean> = isFavorite

    private var timerJob: Job? = null

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

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayerState.value is MediaPlayerState.Playing) {
                delay(DELAY_MILLIS)
                mediaPlayerState.postValue(MediaPlayerState.Playing(mediaPlayerInteractor.getPlayerTime()))
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 500L
    }
}