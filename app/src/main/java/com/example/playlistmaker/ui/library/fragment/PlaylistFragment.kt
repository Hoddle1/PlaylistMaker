package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayListBinding
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.ui.library.adapter.PlaylistAdapter
import com.example.playlistmaker.ui.library.view_model.PlayListViewModel
import com.example.playlistmaker.ui.library.view_model.PlaylistState
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

        binding.mbNewPlaylistButton.setOnClickListener{
            findNavController().navigate(
                R.id.action_libraryFragment_to_addPlaylistFragment
            )
        }

        viewModel.getFavoriteTracksState().observe(viewLifecycleOwner) { state ->
            when(state){
                is PlaylistState.Content -> {
                    playlists.clear()
                    playlists.addAll(state.playlists)
                    playlistAdapter.notifyDataSetChanged()
                    showTracks()
                }
                is PlaylistState.Empty -> showPlaceholder()
            }
        }

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