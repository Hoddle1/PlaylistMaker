package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { search() }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val HISTORY_LIMIT = 10
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    enum class SearchStatus {
        NORMAL, NOT_FOUND, NO_INTERNET
    }


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
            if (clickDebounce()) {
                addTrackHistory(track)
                startPlayerActivity(track)
                updateTrackHistoryList()
            }
        }
        trackHistoryAdapter.onItemClickListener = { track ->
            if (clickDebounce()) {
                addTrackHistory(track)
                startPlayerActivity(track)
                updateTrackHistoryList()
            }
        }

        binding.clearIcon.setOnClickListener {
            clearSearchText()
            binding.tracksList.isVisible = false
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        binding.queryInput.setOnFocusChangeListener { _, hasFocus ->
            updateTrackHistoryList()
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

        binding.clearHistoryButton.setOnClickListener {
            binding.trackHistoryContainer.isVisible = false
            tracksHistory.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            clearTrackHistory()

        }

        binding.queryInput.doOnTextChanged { s, _, _, _ ->
            searchDebounce()
            binding.clearIcon.isVisible = !s.isNullOrEmpty()
            if (s?.isEmpty() == true) {
                hidePlaceholder()
                binding.tracksList.isVisible = false
            }
            searchText = s.toString()
            binding.trackHistoryContainer.isVisible =
                (binding.queryInput.hasFocus() && s?.isEmpty() == true && tracksHistory.isNotEmpty())


        }

        binding.tracksList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter


        binding.historyList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.historyList.adapter = trackHistoryAdapter

    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(Constants.TRACK_DATA, track)
        startActivity(intent)
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
        if (binding.queryInput.text.toString().isNotEmpty()) {
            hidePlaceholder()
            binding.tracksList.isVisible = false
            binding.trackHistoryContainer.isVisible = false
            binding.progressContainer.isVisible = true

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
    }

    private fun showMessage(status: SearchStatus) {
        binding.progressContainer.isVisible = false
        when (status) {
            SearchStatus.NORMAL -> {
                binding.tracksList.isVisible = true
                hidePlaceholder()
            }

            SearchStatus.NOT_FOUND -> {
                binding.placeholderContainer.isVisible = true
                binding.tracksList.isVisible = false
                binding.placeholderText.text = getString(R.string.not_found)
                binding.placeholderText.isVisible = true
                binding.placeholderImage.setImageResource(R.drawable.not_found)
                binding.placeholderImage.isVisible = true
                binding.placeholderUpdateButton.isVisible = false
                binding.trackHistoryContainer.isVisible = false
            }

            SearchStatus.NO_INTERNET -> {
                binding.placeholderContainer.isVisible = true
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

    private fun addTrackHistory(track: Track) {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTrackHistoryList() {
        tracksHistory.clear()
        tracksHistory.addAll(getTrackHistory())
        trackHistoryAdapter.notifyDataSetChanged()
    }

    private fun hidePlaceholder() {
        binding.placeholderContainer.isVisible = false
        binding.placeholderText.isVisible = false
        binding.placeholderImage.isVisible = false
        binding.placeholderUpdateButton.isVisible = false
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }


}