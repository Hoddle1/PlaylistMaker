package com.example.playlistmaker.presentation.ui.search.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.player.activity.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.search.screens.SearchScreen

class SearchFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                SearchScreen(
                    startPlayerActivity = { track ->
                        startPlayerActivity(track)
                    }
                )
            }
        }
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_mediaPlayerActivity,
            MediaPlayerActivity.createArgs(track)
        )
    }

}