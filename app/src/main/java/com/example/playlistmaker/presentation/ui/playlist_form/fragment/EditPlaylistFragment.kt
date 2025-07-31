package com.example.playlistmaker.presentation.ui.playlist_form.fragment

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.presentation.ui.playlist_form.base.BasePlaylistFragment
import com.example.playlistmaker.presentation.ui.playlist_form.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : BasePlaylistFragment() {
    override val viewModel by viewModel<EditPlaylistViewModel>()
    override val screenTitle: String
        get() = getString(R.string.edit_playlist_header)
    override val actionButtonText: String
        get() = getString(R.string.save_playlist)

    override val isFromActivity: Boolean
        get() = false

    private lateinit var playlist: Playlist

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requireArguments().getParcelable(ARG_PLAYLIST, Playlist::class.java)!!
        else
            requireArguments().getParcelable(ARG_PLAYLIST)!!

        viewModel.loadPlaylist(playlist)

        viewModel.playlist.observe(viewLifecycleOwner) { observePlaylist ->
            binding.etPlaylistName.setText(observePlaylist.name)
            binding.etPlaylistDescription.setText(observePlaylist.description)
            observePlaylist.coverImagePath?.let { uri ->
                binding.bPhotoPicker.setImageURI(Uri.parse(uri))
            }
        }

        binding.etPlaylistName.setText(playlist.name)
        binding.etPlaylistDescription.setText(playlist.description)
    }

    override fun onSaveClicked() {
        val updatedName = binding.etPlaylistName.text.toString()
        val updatedDescription =
            binding.etPlaylistDescription.text.toString().takeIf { it.isNotBlank() }
        val updatedCover =
            coverImagePath?.let { viewModel.saveCoverImage(it, updatedName).toString() }
                ?: playlist.coverImagePath

        viewModel.savePlaylist(
            name = updatedName,
            description = updatedDescription,
            coverImagePath = updatedCover
        )
        closeFragment()
    }

    companion object {
        private const val ARG_PLAYLIST = "ARG_PLAYLIST"

        fun createArgs(playlist: Playlist): Bundle = bundleOf(ARG_PLAYLIST to playlist)
    }
}