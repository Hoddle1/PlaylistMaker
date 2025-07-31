package com.example.playlistmaker.presentation.ui.playlist.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.player.activity.MediaPlayerActivity
import com.example.playlistmaker.presentation.ui.playlist.adapter.TracksAdapter
import com.example.playlistmaker.presentation.ui.playlist.entity.ShareEvent
import com.example.playlistmaker.presentation.ui.playlist.entity.UiEvent
import com.example.playlistmaker.presentation.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.presentation.ui.playlist_form.fragment.EditPlaylistFragment
import com.example.playlistmaker.presentation.util.UiMessageHelper
import com.example.playlistmaker.presentation.util.Utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException(getString(R.string.binding_is_null))

    private val tracksAdapter = TracksAdapter()

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var currentDialog: AlertDialog? = null

    private var tracksSlideOffset = 0f

    private var menuSlideOffset = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val insets = ViewCompat.getRootWindowInsets(binding.root)
            ?.getInsets(WindowInsetsCompat.Type.systemBars())

        val playlistId = requireArguments().getInt(PLAYLIST_ID)
        viewModel.loadPlaylist(playlistId)

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            startPlayerActivity(track)
        }

        tracksAdapter.onItemClickListener = onTrackClickDebounce

        tracksAdapter.onItemLongClickListener = { track ->
            viewModel.showDeleteDialog(track, getString(R.string.want_delete_track))
        }

        val bottomSheetBehaviorTracks =
            BottomSheetBehavior.from(binding.flBottomSheetTracks).apply {


                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            state = BottomSheetBehavior.STATE_COLLAPSED
                        }

                        if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            tracksSlideOffset = 0f
                            updateOverlay()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        tracksSlideOffset = slideOffset.coerceIn(0f, 1f)
                        updateOverlay()
                    }
                })
            }

        binding.iBtnShare.post {
            val targetElement = binding.iBtnShare.bottom
            Log.i("targetElement", targetElement.toString())
            val screenHeight = resources.displayMetrics.heightPixels
            Log.i("screenHeight", screenHeight.toString())
            val peekHeight =
                screenHeight - targetElement - (insets?.top ?: 0) - (insets?.bottom
                    ?: 0)
            Log.i("mypeekHeight", peekHeight.toString())
            bottomSheetBehaviorTracks.peekHeight = maxOf(peekHeight, MIN_PEEK_HEIGHT)
            bottomSheetBehaviorTracks.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val bottomSheetBehaviorMenu = BottomSheetBehavior.from(binding.flBottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        menuSlideOffset = 0f
                        updateOverlay()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    menuSlideOffset = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
                    updateOverlay()
                }
            })
        }

        viewModel.playlistState.observe(viewLifecycleOwner) { playlist ->
            binding.tvName.text = playlist.name
            binding.tvMenuPlaylistName.text = playlist.name

            binding.tvDescription.apply {
                text = playlist.description
                isVisible = !playlist.description.isNullOrEmpty()
            }

            binding.tvMenuPlaylistDescription.apply {
                text = playlist.description
                isVisible = !playlist.description.isNullOrEmpty()
            }

            Glide.with(this)
                .load(playlist.coverImagePath)
                .fitCenter()
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .into(binding.ivCover)

            Glide.with(this)
                .load(playlist.coverImagePath)
                .fitCenter()
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .into(binding.ivMenuCover)

        }


        viewModel.tracksState.observe(viewLifecycleOwner) { state ->
            val hasTracks = state.tracks.isNotEmpty()
            if (hasTracks) {
                binding.tvCountTracks.text = resources.getQuantityString(
                    R.plurals.tracks,
                    state.tracks.size,
                    state.tracks.size
                )

                binding.tvDuration.text = resources.getQuantityString(
                    R.plurals.minutes,
                    state.totalDurationMinutes,
                    state.totalDurationMinutes
                )

                tracksAdapter.submitList(state.tracks)
                hidePlaceholder()
            } else {
                binding.tvCountTracks.text = resources.getQuantityString(
                    R.plurals.tracks, 0, 0
                )

                binding.tvDuration.text = resources.getQuantityString(
                    R.plurals.minutes, 0, 0
                )
                showPlaceholder()
            }
        }

        viewModel.uiEventLiveData.observe(viewLifecycleOwner) { event ->
            when (event) {
                is UiEvent.ShowDialog -> showMaterialDialog(event)
                is UiEvent.DismissDialog -> currentDialog?.dismiss()
                is UiEvent.CloseScreen -> closeFragment()
            }
        }

        viewModel.shareEventLiveData.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ShareEvent.ShowToast -> {
                    UiMessageHelper(requireContext())
                        .showCustomSnackbar(event.message)
                }

                is ShareEvent.ShareText -> {
                    shareText(event.text)
                }
            }
        }

        binding.rvTrackItems.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = tracksAdapter
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.iBtnBack.setOnClickListener { closeFragment() }

        binding.iBtnShare.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.ibMenu.setOnClickListener {
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.mbShare.setOnClickListener {

            viewModel.sharePlaylist()
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.mbEditInfo.setOnClickListener {
            viewModel.playlistState.value?.let { playlist ->
                startEditPlaylistFragment(playlist)
            }
        }

        binding.mbDeletePlaylist.setOnClickListener {
            viewModel.deletePlaylist(
                getString(R.string.want_delete_playlist, binding.tvName.text)
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    closeFragment()
                }
            })
    }

    private fun shareText(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
    }

    private fun startPlayerActivity(track: Track) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_mediaPlayerActivity,
            MediaPlayerActivity.createArgs(track)
        )
    }

    private fun startEditPlaylistFragment(playlist: Playlist) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_editPlaylistFragment,
            EditPlaylistFragment.createArgs(playlist)
        )
    }

    private fun showMaterialDialog(event: UiEvent.ShowDialog) {
        val dialogView = layoutInflater.inflate(R.layout.delete_tarack_dialog, null)
        dialogView.findViewById<TextView>(R.id.dialog_message).text = event.message

        dialogView.findViewById<TextView>(R.id.button_yes).setOnClickListener {
            event.onConfirm()
            currentDialog?.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.button_no).setOnClickListener {
            event.onCancel()
            currentDialog?.dismiss()
        }

        currentDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .show()
    }

    private fun closeFragment() {
        findNavController().popBackStack()
    }

    private fun updateOverlay() {
        val maxOffset = maxOf(tracksSlideOffset, menuSlideOffset)
        binding.overlay.apply {
            alpha = maxOffset
            visibility = if (maxOffset > 0f) View.VISIBLE else View.GONE
        }
    }

    private fun showPlaceholder() {
        binding.rvTrackItems.isVisible = false
        binding.placeholder.isVisible = true
    }

    private fun hidePlaceholder() {
        binding.rvTrackItems.isVisible = true
        binding.placeholder.isVisible = false
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val MIN_PEEK_HEIGHT = 200

        private const val PLAYLIST_ID = "PLAYLIST_ID"

        fun createArgs(playlistId: Int): Bundle = bundleOf(PLAYLIST_ID to playlistId)
    }


}