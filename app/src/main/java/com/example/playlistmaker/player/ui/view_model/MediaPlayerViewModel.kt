package com.example.playlistmaker.player.ui.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.MediaPlayerInteractor


class MediaPlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor
) : ViewModel() {

    private var currentTrackTime = MutableLiveData<String>()
    fun getCurrentTrackTime(): LiveData<String> = currentTrackTime

    private var mediaPlayerState = MutableLiveData<MediaPlayerState>(MediaPlayerState.Default)
    fun getMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    private var mainThreadHandler = Handler(Looper.getMainLooper())

    private var timerRunnable: Runnable? = null

    fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(
            url = url,
            onPrependListener = {
                mediaPlayerState.postValue(MediaPlayerState.Prepared)
            },
            onCompletionListener = {
                mediaPlayerState.postValue(MediaPlayerState.Prepared)
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        mediaPlayerState.postValue(MediaPlayerState.Playing)
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        mediaPlayerState.postValue(MediaPlayerState.Paused)
    }

    fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
    }

    fun playbackControl() {
        when (mediaPlayerState.value) {
            MediaPlayerState.Paused, MediaPlayerState.Prepared -> {
                startPlayer()
                startTimer()
            }

            MediaPlayerState.Playing -> {
                pausePlayer()
                stopTimer()
            }

            else -> {}
        }
    }

    private fun startTimer() {
        createUpdateTimerTask().also {
            timerRunnable = it
            mainThreadHandler.post(it)
        }
    }

    fun stopTimer() {
        timerRunnable?.let { mainThreadHandler.removeCallbacks(it) }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (mediaPlayerState.value == MediaPlayerState.Playing) {
                    currentTrackTime.postValue(mediaPlayerInteractor.getPlayerTime())
                    mainThreadHandler.postDelayed(this, DELAY_MILLIS)
                }
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 500L

//        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor(MediaPlayer())
//
//                MediaPlayerViewModel(
//                    mediaPlayerInteractor
//                )
//            }
//        }
    }
}