package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.Constants
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.TracksSearchInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.player.PlayerActivity


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private var searchText: String = ""

    private val tracks: MutableList<Track> = mutableListOf()
    private val tracksHistory: MutableList<Track> = mutableListOf()

    private val trackHistoryAdapter = TrackAdapter(tracksHistory)

    private val trackAdapter = TrackAdapter(tracks)

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { search() }

    private val localHistoryInteractor = Creator.provideTracksHistoryInteractor()
    private val loadTracksUseCase = Creator.provideTracksSearchInteractor()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.llMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.iBtnBack.setOnClickListener { finish() }

        trackAdapter.onItemClickListener = { handleTrackClick(it) }

        trackHistoryAdapter.onItemClickListener = { handleTrackClick(it) }

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

        binding.btnPlaceholder.setOnClickListener {
            search()
        }

        binding.clearHistoryButton.setOnClickListener {
            binding.trackHistoryContainer.isVisible = false
            tracksHistory.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            localHistoryInteractor.clear()

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

            loadTracksUseCase.searchTracks(
                binding.queryInput.text.toString(),
                object : TracksSearchInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?) {
                        handler.post {
                            if (foundTracks == null) {
                                showMessage(SearchStatus.NO_INTERNET)
                            } else if (foundTracks.isNotEmpty()) {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                                trackAdapter.notifyDataSetChanged()
                                showMessage(SearchStatus.NORMAL)
                            } else {
                                showMessage(SearchStatus.NOT_FOUND)
                            }
                        }
                    }
                }
            )
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
                binding.llPlaceholder.isVisible = true
                binding.tracksList.isVisible = false
                binding.tvPlaceholder.text = getString(R.string.not_found)
                binding.tvPlaceholder.isVisible = true
                binding.ivPlaceholder.setImageResource(R.drawable.not_found)
                binding.ivPlaceholder.isVisible = true
                binding.btnPlaceholder.isVisible = false
                binding.trackHistoryContainer.isVisible = false
            }

            SearchStatus.NO_INTERNET -> {
                binding.llPlaceholder.isVisible = true
                binding.tracksList.isVisible = false
                binding.tvPlaceholder.text = getString(R.string.no_internet)
                binding.tvPlaceholder.isVisible = true
                binding.ivPlaceholder.setImageResource(R.drawable.no_internet)
                binding.ivPlaceholder.isVisible = true
                binding.btnPlaceholder.isVisible = true
                binding.trackHistoryContainer.isVisible = false
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTrackHistoryList() {
        tracksHistory.clear()
        tracksHistory.addAll(localHistoryInteractor.getTracks())
        trackHistoryAdapter.notifyDataSetChanged()
    }

    private fun hidePlaceholder() {
        binding.llPlaceholder.isVisible = false
        binding.tvPlaceholder.isVisible = false
        binding.ivPlaceholder.isVisible = false
        binding.btnPlaceholder.isVisible = false
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    private fun handleTrackClick(track: Track) {
        if (clickDebounce()) {
            localHistoryInteractor.saveTrack(track)
            startPlayerActivity(track)
            updateTrackHistoryList()
        }
    }

    enum class SearchStatus {
        NORMAL, NOT_FOUND, NO_INTERNET
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}