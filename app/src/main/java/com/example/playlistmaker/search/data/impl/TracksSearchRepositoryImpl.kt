package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.StatusCodesApi.NETWORK_ERROR_CODE
import com.example.playlistmaker.search.data.StatusCodesApi.SUCCESS_REQUEST_CODE
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TrackSearchResponse
import com.example.playlistmaker.search.data.mapper.TrackSearchMapper
import com.example.playlistmaker.search.domain.TracksSearchRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) :
    TracksSearchRepository {
    override fun searchTracks(term: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(term))

        when (response.resultCode) {
            NETWORK_ERROR_CODE -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            SUCCESS_REQUEST_CODE -> {
                with(response as TrackSearchResponse) {
                    val data = TrackSearchMapper.map(response.results)
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}