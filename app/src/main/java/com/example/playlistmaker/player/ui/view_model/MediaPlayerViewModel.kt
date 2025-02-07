package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor
) : ViewModel() {

    private var mediaPlayerState = MutableLiveData<MediaPlayerState>(MediaPlayerState.Default())
    fun getMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

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
                mediaPlayerState.postValue(MediaPlayerState.Prepared())
            }
        )
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