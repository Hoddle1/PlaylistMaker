package com.example.playlistmaker.presentation.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsLibraryBinding
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.presentation.ui.library.adapter.PlaylistAdapter
import com.example.playlistmaker.presentation.ui.library.view_model.PlaylistState
import com.example.playlistmaker.presentation.ui.library.view_model.PlaylistsLibraryViewModel
import com.example.playlistmaker.presentation.ui.playlist.fragment.PlaylistFragment
import com.example.playlistmaker.presentation.util.GridSpacingItemDecoration
import com.example.playlistmaker.presentation.util.Utils
import com.example.playlistmaker.presentation.util.Utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsLibraryFragment : Fragment() {
    private var _binding: FragmentPlaylistsLibraryBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException(getString(R.string.binding_is_null))

    private val playlistAdapter = PlaylistAdapter()

    private val viewModel by viewModel<PlaylistsLibraryViewModel>()

    private lateinit var onTrackClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            startPlaylistDetailFragment(playlist)
        }

        playlistAdapter.onItemClickListener = { onTrackClickDebounce(it) }

        binding.mbNewPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_addPlaylistFragment
            )
        }

        viewModel.getPlaylistsState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    playlistAdapter.submitList(state.playlists)
                    showTracks()
                }

                is PlaylistState.Empty -> showPlaceholder()
            }
        }

        binding.rvPlaylist.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.rvPlaylist.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = 2,
                spacing = Utils.dpToPx(8f, requireContext()),
                false
            )
        )
        binding.rvPlaylist.adapter = playlistAdapter
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    private fun showPlaceholder() {
        binding.llPlaceholder.isVisible = true
        binding.rvPlaylist.isVisible = false
    }

    private fun showTracks() {
        binding.llPlaceholder.isVisible = false
        binding.rvPlaylist.isVisible = true
    }

    private fun startPlaylistDetailFragment(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playlistFragment,
            PlaylistFragment.createArgs(playlist.id)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

        fun newInstance() = PlaylistsLibraryFragment()
    }

}