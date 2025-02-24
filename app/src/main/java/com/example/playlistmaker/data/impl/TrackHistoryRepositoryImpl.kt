package com.example.playlistmaker.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.search.TrackHistoryRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : TrackHistoryRepository {

    override fun saveTrack(track: Track) {
        val history = getTracks()

        history.remove(track)

        if (history.size >= HISTORY_LIST_LIMIT) {
            history.removeAt(history.lastIndex)
        }

        history.add(0, track)

        val historyJson = gson.toJson(history)

        sharedPreferences.edit()
            .putString("search_history", historyJson)
            .apply()
    }

    override fun getTracks(): MutableList<Track> {
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

    companion object {
        private const val HISTORY_LIST_LIMIT = 10
    }
}