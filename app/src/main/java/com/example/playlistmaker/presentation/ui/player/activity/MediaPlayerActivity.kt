package com.example.playlistmaker.presentation.ui.player.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.presentation.ui.player.adapter.PlaylistsAdapter
import com.example.playlistmaker.presentation.ui.player.view_model.BottomSheetState
import com.example.playlistmaker.presentation.ui.player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.presentation.ui.playlistadd.fragment.AddPlaylistFragment
import com.example.playlistmaker.presentation.util.UiMessageHelper
import com.example.playlistmaker.presentation.util.Utils
import com.example.playlistmaker.presentation.util.Utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<MediaPlayerViewModel>()

    private val playlists: MutableList<Playlist> = mutableListOf()

    private val playlistsAdapter = PlaylistsAdapter(playlists)

    private val uiMessageHelper: UiMessageHelper by inject{ parametersOf(this)}

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.svMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(TRACK_DATA, Track::class.java)!!
        else
            (intent.getParcelableExtra(TRACK_DATA) as? Track)!!

        onPlaylistClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            lifecycleScope,
            false
        ) { playlist ->
            viewModel.addTrackToPlaylist(playlist, track)
        }

        playlistsAdapter.onItemClickListener = { onPlaylistClickDebounce(it) }

        binding.rvPlaylistItems.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPlaylistItems.adapter = playlistsAdapter

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.llBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = ((slideOffset + 1f) / 2f).coerceIn(0f, 1f)
                binding.overlay.alpha = alpha
            }
        })

        viewModel.getBottomSheetState().observe(this) { state ->
            when (state) {
                BottomSheetState.Hidden -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN

                is BottomSheetState.Collapsed -> {
                    playlists.clear()
                    playlists.addAll(state.playlists)
                    playlistsAdapter.notifyDataSetChanged()

                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

        viewModel.getMediaPlayerState().observe(this) { state ->
            binding.iBtnPlay.setImageResource(state.imageResource)
            binding.tvCurrentTrackTime.text = state.progress
        }

        viewModel.getIsFavorite().observe(this) { isFavorite ->
            setFavoriteButton(isFavorite)
        }

        viewModel.getUiMessageState().observe(this) { message ->
            uiMessageHelper.showCustomSnackbar(
                view = view,
                message = message
            )
        }

        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.hideBottomSheet()
        }

        viewModel.preparePlayer(track.previewUrl)

        setFavoriteButton(track.isFavorite)

        supportFragmentManager.addOnBackStackChangedListener {
            binding.fragmentContainer.visibility =
                if (supportFragmentManager.backStackEntryCount == 0) View.GONE else View.VISIBLE
        }

        with(binding) {
            textView.text = track.trackName
            artistName.text = track.artistName
            tvDurationValue.text = track.trackTimeMillis

            if (track.collectionName == null) {
                tvCollectionNameValue.isVisible = false
                tvCollectionNameTitle.isVisible = false
            } else {
                tvCollectionNameValue.text = track.collectionName
            }

            if (track.releaseDate == null) {
                tvReleaseDateValue.isVisible = false
                tvReleaseDateValue.isVisible = false
            } else {
                tvReleaseDateValue.text =
                    SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
            }

            tvGenreValue.text = track.primaryGenreName
            tvCountryValue.text = track.country

            Glide.with(this@MediaPlayerActivity)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .fitCenter()
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        Utils.dpToPx(
                            8f,
                            this@MediaPlayerActivity.applicationContext
                        )
                    )
                )
                .into(ivCover)


            iBtnPlay.setOnClickListener {
                viewModel.playbackControl()
            }

            iBtnFavorite.setOnClickListener {
                viewModel.onFavoriteClicked(track)
            }

            iBtnQueue.setOnClickListener {
                viewModel.getPlaylists()
            }

            mbNewPlaylistButton.setOnClickListener{
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddPlaylistFragment.newInstance())
                    .addToBackStack(null)
                    .commit()

                binding.fragmentContainer.visibility = View.VISIBLE
            }

            iBtnBack.setOnClickListener { finish() }
        }
    }

    private fun setFavoriteButton(isFavorite: Boolean) {
        binding.iBtnFavorite.setImageResource(
            if (isFavorite) {
                R.drawable.favorite_button_active
            } else {
                R.drawable.favorite_button_inactive
            }
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val TRACK_DATA = "TRACK_DATA"

        fun createArgs(track: Track): Bundle = bundleOf(TRACK_DATA to track)

    }

}