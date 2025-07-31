package com.example.playlistmaker.presentation.ui.playlist_form.fragment

import android.os.Bundle
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.playlist_form.base.BasePlaylistFragment
import com.example.playlistmaker.presentation.ui.playlist_form.view_model.AddPlaylistViewModel
import com.example.playlistmaker.presentation.util.UiMessageHelper
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddPlaylistFragment : BasePlaylistFragment() {
    override val viewModel by viewModel<AddPlaylistViewModel>()
    override val screenTitle: String
        get() = getString(R.string.new_playlist_header)
    override val actionButtonText: String
        get() = getString(R.string.create_playlist)
    override val isFromActivity: Boolean
        get() = arguments?.getBoolean(ARG_IS_FROM_ACTIVITY) ?: false

    private val uiMessageHelper: UiMessageHelper by inject { parametersOf(requireContext()) }

    override fun onSaveClicked() {
        val name = binding.etPlaylistName.text.toString()
        val description = binding.etPlaylistDescription.text.toString().takeIf { it.isNotBlank() }
        val coverImage = coverImagePath?.let { viewModel.saveCoverImage(it, name) }

        viewModel.savePlaylist(
            name = name,
            description = description,
            coverImagePath = coverImage?.toString()
        )

        uiMessageHelper.showCustomSnackbar(getString(R.string.playlist_added, name))
        closeFragment()
    }

    companion object {
        private const val ARG_IS_FROM_ACTIVITY = "is_from_activity"

        fun newInstance(): AddPlaylistFragment {
            val fragment = AddPlaylistFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(ARG_IS_FROM_ACTIVITY, true)
            }
            return fragment
        }
    }
}