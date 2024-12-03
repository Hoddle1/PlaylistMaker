package com.example.playlistmaker.data

import com.example.playlistmaker.data.model.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}