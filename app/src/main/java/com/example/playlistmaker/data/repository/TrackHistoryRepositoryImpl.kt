package com.example.playlistmaker.data.repository

import android.content.Context
import com.example.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.example.playlistmaker.domain.api.TrackHistoryRepository
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class TrackHistoryRepositoryImpl(context: Context) : TrackHistoryRepository {

    private val sharedPreferences =
        context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val HISTORY_LIST_LIMIT = 10

    override fun saveTrack(track: Track) {
        val history = getTracks()

        history.remove(track)

        if (history.size >= HISTORY_LIST_LIMIT) {
            history.removeLast()
        }

        history.add(0, track)

        val historyJson = gson.toJson(history)

        sharedPreferences.edit()
            .putString("search_history", historyJson)
            .apply()
    }

    override fun getTracks(): MutableList<Track> {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val history: MutableList<Track>

        val json = sharedPreferences.getString("search_history", null)

        if (json == null) {
            history = mutableListOf()
        } else {
            val itemType = object : TypeToken<MutableList<Track>>() {}.type
            history = gson.fromJson(json, itemType)
        }

        return history
    }

    override fun clear() {
        sharedPreferences.edit()
            .remove("search_history")
            .apply()
    }
}