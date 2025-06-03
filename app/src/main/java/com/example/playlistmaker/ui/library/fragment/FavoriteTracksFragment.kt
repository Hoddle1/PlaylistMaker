package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.library.adapter.FavoriteTrackAdapter
import com.example.playlistmaker.ui.library.view_model.FavoriteTracksState
import com.example.playlistmaker.ui.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.ui.player.activity.MediaPlayerActivity
import com.example.playlistmaker.util.Utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null

    private val binding get() = _binding!!

    private val favoriteTracks: MutableList<Track> = mutableListOf()

    private val favoriteTrackAdapter = FavoriteTrackAdapter(favoriteTracks)

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val viewModel by viewModel<FavoriteTracksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.saveTrackOnHistory(track)
            startPlayerActivity(track)
        }

        favoriteTrackAdapter.onItemClickListener = { onTrackClickDebounce(it) }

        viewModel.getFavoriteTracksState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteTracksState.Content -> {
                    favoriteTracks.clear()
                    favoriteTracks.addAll(state.tracks)
                    favoriteTrackAdapter.notifyDataSetChanged()
                    showTracks()
                }

                is FavoriteTracksState.Empty -> showPlaceholder()
            }
        }

        binding.favoriteTracksList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoriteTracksList.adapter = favoriteTrackAdapter

        viewModel.getFavoriteTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPlaceholder() {
        binding.llPlaceholder.isVisible = true
        binding.favoriteTracksList.isVisible = false
    }

    private fun showTracks() {
        binding.llPlaceholder.isVisible = false
        binding.favoriteTracksList.isVisible = true
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_mediaPlayerActivity,
            MediaPlayerActivity.createArgs(track)
        )
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        fun newInstance() = FavoriteTracksFragment()
    }
}