package com.example.playlistmaker.presentation.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.library.screens.LibraryScreen
import com.example.playlistmaker.presentation.ui.player.activity.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.playlist.fragment.PlaylistFragment


class LibraryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

                LibraryScreen(
                    onStartPlayerActivity = { track ->
                        startPlayerActivity(track)
                    },
                    onStartPlaylistDetailFragment = { playlsit ->
                        startPlaylistDetailFragment(playlsit)
                    },
                    onStartNewPlaylistFragment = { startNewPlaylistFragment() }
                )
            }
        }
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_mediaPlayerActivity,
            MediaPlayerActivity.createArgs(track)
        )
    }

    private fun startPlaylistDetailFragment(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playlistFragment,
            PlaylistFragment.createArgs(playlist.id)
        )
    }

    private fun startNewPlaylistFragment() {
        findNavController().navigate(
            R.id.action_libraryFragment_to_addPlaylistFragment
        )
    }


}

