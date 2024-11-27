package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.StatusCodesApi
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.model.Response
import com.example.playlistmaker.data.model.TrackSearchRequest
import com.google.gson.GsonBuilder
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

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = StatusCodesApi.NETWORK_ERROR_CODE }
        }

        if (dto !is TrackSearchRequest) {
            return Response().apply { resultCode = StatusCodesApi.BAD_REQUEST_CODE }
        }

        try {
            val resp = iTunesService.searchTracks(dto.term).execute()
            val body = resp.body() ?: Response()
            return body.apply { resultCode = resp.code() }
        } catch (ex: Exception) {
            return Response().apply { resultCode = StatusCodesApi.NETWORK_ERROR_CODE }
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