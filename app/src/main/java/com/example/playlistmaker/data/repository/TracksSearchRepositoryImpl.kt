package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.StatusCodesApi
import com.example.playlistmaker.data.mapper.TrackSearchMapper
import com.example.playlistmaker.data.model.TrackSearchRequest
import com.example.playlistmaker.data.model.TrackSearchResponse
import com.example.playlistmaker.domain.api.TracksSearchRepository
import com.example.playlistmaker.domain.model.Track

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