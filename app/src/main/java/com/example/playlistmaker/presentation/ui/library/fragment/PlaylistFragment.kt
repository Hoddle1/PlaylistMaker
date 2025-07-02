package com.example.playlistmaker.presentation.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayListBinding
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.presentation.ui.library.adapter.PlaylistAdapter
import com.example.playlistmaker.presentation.ui.library.view_model.PlayListViewModel
import com.example.playlistmaker.presentation.ui.library.view_model.PlaylistState
import com.example.playlistmaker.presentation.util.GridSpacingItemDecoration
import com.example.playlistmaker.presentation.util.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlayListBinding? = null

    private val binding get() = _binding!!

    private val playlists: MutableList<Playlist> = mutableListOf()

    private val playlistAdapter = PlaylistAdapter(playlists)

    private val viewModel by viewModel<PlayListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mbNewPlaylistButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_addPlaylistFragment
            )
        }

        viewModel.getPlaylistsState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    playlists.clear()
                    playlists.addAll(state.playlists)
                    playlistAdapter.notifyDataSetChanged()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }

}