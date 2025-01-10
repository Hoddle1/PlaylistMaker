package com.example.playlistmaker.search.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.activity.MediaPlayerActivity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.adapter.TrackAdapter
import com.example.playlistmaker.search.ui.view_model.ErrorSearchStatus
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.view_model.TrackListState
import com.example.playlistmaker.util.Constants


class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val tracks: MutableList<Track> = mutableListOf()

    private val tracksHistory: MutableList<Track> = mutableListOf()

    private val trackHistoryAdapter = TrackAdapter(tracksHistory)

    private val trackAdapter = TrackAdapter(tracks)

    private val viewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

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
            if (hasFocus && binding.queryInput.text.isEmpty()) {
                viewModel.queryInputOnFocused()
            }
        }

        binding.btnPlaceholder.setOnClickListener {
            viewModel.search(binding.queryInput.text.toString())
        }

        binding.clearHistoryButton.setOnClickListener {
            binding.trackHistoryContainer.isVisible = false
            tracksHistory.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            viewModel.clearHistory()
        }

        binding.queryInput.doOnTextChanged { s, _, _, _ ->
            binding.clearIcon.isVisible = !s.isNullOrEmpty()
            viewModel.onTextChange(s.toString())
        }

        binding.tracksList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

        binding.historyList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.historyList.adapter = trackHistoryAdapter

        viewModel.getTrackListState().observe(this) { state ->
            when (state) {
                is TrackListState.Content -> {
                    tracks.clear()
                    tracks.addAll(state.tracks)
                    trackAdapter.notifyDataSetChanged()
                    showTracks()
                }

                is TrackListState.Error -> {
                    showError(state.status)
                }

                is TrackListState.History -> {
                    updateTrackHistoryList(state.tracks)
                    if (tracksHistory.isNotEmpty()) {
                        showHistory()
                    } else {
                        hideAll()
                    }

                }

                TrackListState.Loading -> {
                    showProgress()
                }
            }
        }
    }


    private fun handleTrackClick(track: Track) {
        if (viewModel.clickDebounce()) {
            viewModel.saveTrack(track)
            startPlayerActivity(track)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTrackHistoryList(tracks: List<Track>) {
        tracksHistory.clear()
        tracksHistory.addAll(tracks)
        trackHistoryAdapter.notifyDataSetChanged()
    }

    private fun startPlayerActivity(track: Track) {
        val intent = Intent(this, MediaPlayerActivity::class.java)
        intent.putExtra(Constants.TRACK_DATA, track)
        startActivity(intent)
    }

    private fun clearSearchText() {
        binding.queryInput.setText(R.string.empty_string)
    }

    private fun showError(status: ErrorSearchStatus) {
        when (status) {
            ErrorSearchStatus.NOT_FOUND -> {
                binding.progressContainer.isVisible = false
                binding.llPlaceholder.isVisible = true
                binding.tracksList.isVisible = false
                binding.tvPlaceholder.text = getString(R.string.not_found)
                binding.tvPlaceholder.isVisible = true
                binding.ivPlaceholder.setImageResource(R.drawable.not_found)
                binding.ivPlaceholder.isVisible = true
                binding.btnPlaceholder.isVisible = false
                binding.trackHistoryContainer.isVisible = false
            }

            ErrorSearchStatus.NO_INTERNET -> {
                binding.progressContainer.isVisible = false
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

    private fun showProgress() {
        binding.progressContainer.isVisible = true
        binding.llPlaceholder.isVisible = false
        binding.tracksList.isVisible = false
        binding.tvPlaceholder.isVisible = false
        binding.ivPlaceholder.isVisible = false
        binding.btnPlaceholder.isVisible = false
        binding.trackHistoryContainer.isVisible = false
    }

    private fun showHistory() {
        binding.progressContainer.isVisible = false
        binding.llPlaceholder.isVisible = false
        binding.tracksList.isVisible = false
        binding.tvPlaceholder.isVisible = false
        binding.ivPlaceholder.isVisible = false
        binding.btnPlaceholder.isVisible = false
        binding.trackHistoryContainer.isVisible = true
    }

    private fun showTracks() {
        binding.progressContainer.isVisible = false
        binding.llPlaceholder.isVisible = false
        binding.tracksList.isVisible = true
        binding.tvPlaceholder.isVisible = false
        binding.ivPlaceholder.isVisible = false
        binding.btnPlaceholder.isVisible = false
        binding.trackHistoryContainer.isVisible = false
    }

    private fun hideAll() {
        binding.progressContainer.isVisible = false
        binding.llPlaceholder.isVisible = false
        binding.tracksList.isVisible = false
        binding.tvPlaceholder.isVisible = false
        binding.ivPlaceholder.isVisible = false
        binding.btnPlaceholder.isVisible = false
        binding.trackHistoryContainer.isVisible = false
    }

}