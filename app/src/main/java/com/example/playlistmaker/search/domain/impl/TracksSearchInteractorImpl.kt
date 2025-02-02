package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TracksSearchInteractor
import com.example.playlistmaker.search.domain.TracksSearchRepository
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(
    private val repository: TracksSearchRepository
) : TracksSearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(term: String, consumer: TracksSearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(term))
        }
    }

}