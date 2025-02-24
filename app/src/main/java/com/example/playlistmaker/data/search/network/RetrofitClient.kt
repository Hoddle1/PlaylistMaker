package com.example.playlistmaker.data.search.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.search.NetworkClient
import com.example.playlistmaker.data.search.StatusCodesApi
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date


class RetrofitClient(private val context: Context) : NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private var retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .registerTypeAdapter(Date::class.java, CustomDataTypeAdapter())
                    .create()
            )
        )
        .build()

    private val iTunesService = retrofit.create(ITunesApiService::class.java)

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = StatusCodesApi.NETWORK_ERROR_CODE }
        }

        if (dto !is TrackSearchRequest) {
            return Response().apply { resultCode = StatusCodesApi.BAD_REQUEST_CODE }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = iTunesService.searchTracks(dto.term)
                response.apply { resultCode = StatusCodesApi.SUCCESS_REQUEST_CODE }
            } catch (ex: Exception) {
                Response().apply { resultCode = StatusCodesApi.NETWORK_ERROR_CODE }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }
}