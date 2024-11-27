package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.Utils.convertMillisToTime
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.MediaPlayerRepository

class MediaPlayerInteractorImpl(private val repository: MediaPlayerRepository) :
    MediaPlayerInteractor {
    override fun preparePlayer(
        url: String,
        onPrependListener: () -> Unit,
        onCompletionListener: () -> Unit
    ) {
        repository.preparePlayer(
            url,
            onPrependListener,
            onCompletionListener
        )
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun stopPlayer() {
        repository.stopPlayer()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun getPlayerTime(): String {
        return convertMillisToTime(repository.getCurrentPosition())
    }
}