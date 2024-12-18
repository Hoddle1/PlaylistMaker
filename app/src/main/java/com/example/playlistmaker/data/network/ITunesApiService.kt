package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.model.TrackSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TrackSearchResponse>
}