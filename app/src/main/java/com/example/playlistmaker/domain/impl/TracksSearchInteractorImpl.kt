package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksSearchInteractor
import com.example.playlistmaker.domain.api.TracksSearchRepository
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(private val repository: TracksSearchRepository) :
    TracksSearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: TracksSearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(term))
        }
    }

}