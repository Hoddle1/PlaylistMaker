package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.MediaPlayerInteractor
import com.example.playlistmaker.domain.player.MediaPlayerRepository
import com.example.playlistmaker.presentation.util.Utils.convertMillisToTime

class MediaPlayerInteractorImpl(
    private val repository: MediaPlayerRepository
) : MediaPlayerInteractor {

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

    override fun seekTo(position: Int) {
        repository.seekTo(position)
    }

    override fun getPlayerTime(): String {
        return convertMillisToTime(repository.getCurrentPosition())
    }
}