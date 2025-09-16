package com.example.playlistmaker.presentation.ui.search.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.player.activity.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.adapter.TrackAdapter
import com.example.playlistmaker.presentation.ui.search.components.RoundedButton
import com.example.playlistmaker.presentation.ui.search.components.SearchField
import com.example.playlistmaker.presentation.ui.search.components.TrackItem
import com.example.playlistmaker.presentation.ui.search.view_model.ErrorSearchStatus
import com.example.playlistmaker.presentation.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.presentation.ui.search.view_model.TrackListState
import com.example.playlistmaker.presentation.ui.theme.Fonts
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException(getString(R.string.binding_is_null))
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
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                val searchText by viewModel.searchText.collectAsState()
                val state: TrackListState by viewModel.getTrackListState().collectAsState()

                Scaffold(
                    backgroundColor = Color(LocalContext.current.getColor(R.color.default_background))
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                    ) {

                        Header()

                        SearchField(
                            value = searchText,
                            onValueChange = { text ->
                                viewModel.onTextChange(text)
                            },
                            onClear = {
                                viewModel.clearSearch()
                            }
                        )

                        when (val s = state) {
                            is TrackListState.Content -> {
                                TrackList(s.tracks)
                            }

                            is TrackListState.Error -> {
                                ErrorView(s.status)
                            }

                            is TrackListState.History -> {
                                if (tracksHistory.isNotEmpty()) {
                                    TrackHistoryList(s.tracks)
                                } else {
                                    EmptyPlaceholder()
                                }

                            }

                            TrackListState.Loading -> {
                                LoadingView()
                            }

                        }
                    }
                }
            }
        }
    }


    @Composable
    fun Header() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)

        ) {
            Text(
                text = LocalContext.current.getString(R.string.settings),
                color = Color(LocalContext.current.getColor(R.color.settings_text_button)),
                fontWeight = FontWeight.W500,
                fontFamily = Fonts.YSDisplay,
                fontSize = 22.sp
            )
        }
    }

    @Composable
    fun TrackList(
        tracks: List<Track>
    ) {
        LazyColumn {
            items(
                items = tracks,
                key = { it.trackId }
            ) { track ->
                TrackItem(
                    track = track
                )
            }
        }
    }

    @Composable
    fun TrackHistoryList(
        tracks: List<Track>
    ) {
        TrackList(tracks)
        Spacer(Modifier.height(24.dp))
        RoundedButton(
            text = LocalContext.current.getString(R.string.clear_history)
        ) { }
    }


    @Composable
    fun LoadingView() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                color = Color(LocalContext.current.getColor(R.color.progress))
            )
        }
    }

    @Composable
    fun ErrorView(status: ErrorSearchStatus) {
        Text("Error")
    }

    @Composable
    fun EmptyPlaceholder() {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Пусто")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        onTrackClickDebounce = debounce(
//            CLICK_DEBOUNCE_DELAY_MILLIS,
//            viewLifecycleOwner.lifecycleScope,
//            false
//        ) { track ->
//            viewModel.saveTrack(track)
//            startPlayerActivity(track)
//        }
//
//        trackAdapter.onItemClickListener = { onTrackClickDebounce(it) }
//
//        trackHistoryAdapter.onItemClickListener = { onTrackClickDebounce(it) }
//
//        binding.clearIcon.setOnClickListener {
//            clearSearchText()
//            binding.tracksList.isVisible = false
//            tracks.clear()
//            trackAdapter.notifyDataSetChanged()
//        }
//
//        binding.queryInput.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus && binding.queryInput.text.isEmpty()) {
//                viewModel.queryInputOnFocused()
//            }
//        }
//
//        binding.btnPlaceholder.setOnClickListener {
//            viewModel.search(binding.queryInput.text.toString())
//        }
//
//        binding.clearHistoryButton.setOnClickListener {
//            binding.trackHistoryContainer.isVisible = false
//            tracksHistory.clear()
//            trackHistoryAdapter.notifyDataSetChanged()
//            viewModel.clearHistory()
//        }
//
//        binding.queryInput.doOnTextChanged { s, _, _, _ ->
//            binding.clearIcon.isVisible = !s.isNullOrEmpty()
//            viewModel.onTextChange(s.toString())
//        }
//
//        binding.tracksList.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.tracksList.adapter = trackAdapter
//
//        binding.historyList.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.historyList.adapter = trackHistoryAdapter
//
//        viewModel.getTrackListState().observe(viewLifecycleOwner) { state ->
//            when (state) {
//                is TrackListState.Content -> {
//                    tracks.clear()
//                    tracks.addAll(state.tracks)
//                    trackAdapter.notifyDataSetChanged()
//                    showTracks()
//                }
//
//                is TrackListState.Error -> {
//                    showError(state.status)
//                }
//
//                is TrackListState.History -> {
//                    updateTrackHistoryList(state.tracks)
//                    if (tracksHistory.isNotEmpty()) {
//                        showHistory()
//                    } else {
//                        hideAll()
//                    }
//
//                }
//
//                TrackListState.Loading -> {
//                    showProgress()
//                }
//            }
//        }
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