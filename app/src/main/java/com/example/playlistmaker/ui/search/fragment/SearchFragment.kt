package com.example.playlistmaker.ui.search.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.activity.MediaPlayerActivity
import com.example.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.ui.search.view_model.ErrorSearchStatus
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.search.view_model.TrackListState
import com.example.playlistmaker.util.Utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val tracks: MutableList<Track> = mutableListOf()

    private val tracksHistory: MutableList<Track> = mutableListOf()

    private val trackHistoryAdapter = TrackAdapter(tracksHistory)

    private val trackAdapter = TrackAdapter(tracks)

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.saveTrack(track)
            startPlayerActivity(track)
        }

        trackAdapter.onItemClickListener = { onTrackClickDebounce(it) }

        trackHistoryAdapter.onItemClickListener = { onTrackClickDebounce(it) }

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
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

        binding.historyList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.historyList.adapter = trackHistoryAdapter

        viewModel.getTrackListState().observe(viewLifecycleOwner) { state ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateTrackHistoryList(tracks: List<Track>) {
        tracksHistory.clear()
        tracksHistory.addAll(tracks)
        trackHistoryAdapter.notifyDataSetChanged()
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_mediaPlayerActivity,
            MediaPlayerActivity.createArgs(track)
        )
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



    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}