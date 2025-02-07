package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.MediaPlayerRepository
import com.example.playlistmaker.util.Utils.convertMillisToTime

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

    override fun seekTo(time: Int) {
        repository.seekTo(time)
    }

    override fun getPlayerTime(): String {
        return convertMillisToTime(repository.getCurrentPosition())
    }
}