package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.StatusCodesApi
import com.example.playlistmaker.search.data.TracksSearchRepository
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.mapper.TrackSearchMapper
import com.example.playlistmaker.search.domain.model.Track

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) :
    TracksSearchRepository {
    override fun searchTracks(term: String): List<Track>? {
        val response = networkClient.doRequest(TrackSearchRequest(term))

        return if (response.resultCode == StatusCodesApi.SUCCESS_REQUEST_CODE) {
            TrackSearchMapper.map((response as TrackSearchResponse).results)
        } else if (response.resultCode == StatusCodesApi.NETWORK_ERROR_CODE) {
            null
        } else {
            null
        }
    }
}