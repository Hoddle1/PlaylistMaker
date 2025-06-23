package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.StatusCodesApi.NETWORK_ERROR_CODE
import com.example.playlistmaker.data.search.StatusCodesApi.SUCCESS_REQUEST_CODE
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TrackSearchResponse
import com.example.playlistmaker.data.search.mapper.TrackSearchMapper
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.domain.search.TracksSearchRepository
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