package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.RetrofitClient.getClient
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private var searchText: String = ""

    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksHistory: MutableList<Track> = mutableListOf()
    private val trackHistoryAdapter = TrackAdapter(tracksHistory)

    private val trackAdapter = TrackAdapter(tracks)


    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = getClient(iTunesBaseUrl)

    private val iTunesService = retrofit.create(iTunesSearchApi::class.java)


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener { finish() }

        trackAdapter.onItemClickListener = { track ->
            addTrackHistory(track)
            tracksHistory.clear()
            tracksHistory.addAll(getTrackHistory())
            trackHistoryAdapter.notifyDataSetChanged()
        }
        trackHistoryAdapter.onItemClickListener = { track ->
            addTrackHistory(track)
            tracksHistory.clear()
            tracksHistory.addAll(getTrackHistory())
            trackHistoryAdapter.notifyDataSetChanged()
        }

        binding.clearIcon.setOnClickListener {
            clearSearchText()
            binding.tracksList.isVisible = false
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        binding.queryInput.setOnFocusChangeListener { _, hasFocus ->
            tracksHistory.clear()
            tracksHistory.addAll(getTrackHistory())
            trackHistoryAdapter.notifyDataSetChanged()
            binding.trackHistoryContainer.isVisible =
                hasFocus && binding.queryInput.text.isEmpty() && tracksHistory.isNotEmpty()
        }


        savedInstanceState?.let {
            searchText = it.getString(SEARCH_TEXT, "")
            binding.queryInput.setText(searchText)
        }

        binding.placeholderUpdateButton.setOnClickListener {
            search()
        }

        binding.queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        binding.clearHistoryButton.setOnClickListener {
            binding.trackHistoryContainer.isVisible = false
            tracksHistory.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            clearTrackHistory()

        }

        binding.queryInput.doOnTextChanged { s, _, _, _ ->
            binding.clearIcon.isVisible = !s.isNullOrEmpty()
            if (s?.isEmpty() == true) {
                hidePlaceholder()
            }
            searchText = s.toString()
            binding.trackHistoryContainer.isVisible =
                (binding.queryInput.hasFocus() && s?.isEmpty() == true && tracksHistory.isNotEmpty())

            if(s?.isEmpty() == true){
                binding.tracksList.isVisible = false
            }

        }

        binding.tracksList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter


        binding.historyList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.historyList.adapter = trackHistoryAdapter

    }

    private fun clearSearchText() {
        binding.queryInput.setText(R.string.empty_string)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, "")
    }

    private fun search() {
        iTunesService.search(binding.queryInput.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                showMessage(SearchStatus.NORMAL)
                            } else {
                                showMessage(SearchStatus.NOT_FOUND)
                            }
                        }

                        else -> {
                            Log.d("asd", response.code().toString())
                            showMessage(SearchStatus.NO_INTERNET)
                        }
                    }

                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {

                    showMessage(SearchStatus.NO_INTERNET)
                }
            })
    }

    private fun showMessage(status: SearchStatus) {
        when (status) {
            SearchStatus.NORMAL -> {
                binding.tracksList.isVisible = true
                hidePlaceholder()
            }

            SearchStatus.NOT_FOUND -> {
                binding.tracksList.isVisible = false
                binding.placeholderText.text = getString(R.string.not_found)
                binding.placeholderText.isVisible = true
                binding.placeholderImage.setImageResource(R.drawable.not_found)
                binding.placeholderImage.isVisible = true
                binding.placeholderUpdateButton.isVisible = false
                binding.trackHistoryContainer.isVisible = false
            }

            SearchStatus.NO_INTERNET -> {
                binding.tracksList.isVisible = false
                binding.placeholderText.text = getString(R.string.no_internet)
                binding.placeholderText.isVisible = true
                binding.placeholderImage.setImageResource(R.drawable.no_internet)
                binding.placeholderImage.isVisible = true
                binding.placeholderUpdateButton.isVisible = true
                binding.trackHistoryContainer.isVisible = false
            }
        }
    }

    private fun clearTrackHistory() {
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .remove("search_history")
            .apply()
    }

    private fun addTrackHistory(track: Track){
        val history = getTrackHistory()

        if (history.contains(track)) {
            history.remove(track)
        } else if (history.size >= HISTORY_LIMIT) {
            history.removeLast()
        }

        history.add(0, track)

        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        val historyJson = gson.toJson(history)

        sharedPrefs.edit()
            .putString("search_history", historyJson)
            .apply()

    }

    private fun getTrackHistory(): MutableList<Track> {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        val history: MutableList<Track>

        val json = sharedPrefs.getString("search_history", null)

        if (json == null) {
            history = mutableListOf()
        } else {
            val itemType = object : TypeToken<MutableList<Track>>() {}.type
            history = gson.fromJson(json, itemType)
        }

        return history
    }

    private fun hidePlaceholder(){
        binding.placeholderText.isVisible = false
        binding.placeholderImage.isVisible = false
        binding.placeholderUpdateButton.isVisible = false
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val HISTORY_LIMIT = 10
    }

    enum class SearchStatus {
        NORMAL, NOT_FOUND, NO_INTERNET
    }

}