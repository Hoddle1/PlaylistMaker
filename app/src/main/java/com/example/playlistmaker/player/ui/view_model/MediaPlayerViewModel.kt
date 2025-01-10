package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.creator.Creator


class MediaPlayerViewModel : ViewModel() {

    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor(MediaPlayer())

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

    fun onDestroy() {
        stopTimer()
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
        timerRunnable = createUpdateTimerTask()
        mainThreadHandler.post(timerRunnable!!)
    }

    private fun stopTimer() {
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
    }
}