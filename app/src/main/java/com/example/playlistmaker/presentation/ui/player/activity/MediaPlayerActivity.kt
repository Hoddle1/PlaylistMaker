package com.example.playlistmaker.presentation.ui.player.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.example.playlistmaker.presentation.ui.player.view_model.MediaPlayerState
import com.example.playlistmaker.presentation.ui.player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.presentation.ui.playlist_form.fragment.AddPlaylistFragment
import com.example.playlistmaker.presentation.util.UiMessageHelper
import com.example.playlistmaker.presentation.util.Utils
import com.example.playlistmaker.presentation.util.Utils.debounce
import com.example.playlistmaker.services.MusicService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<MediaPlayerViewModel>()

    private val playlistsAdapter = PlaylistsAdapter()

    private val uiMessageHelper: UiMessageHelper by inject { parametersOf(this) }

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            viewModel.setAudioPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerControl()
        }
    }
    private val requestPostNotificationsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestPostNotificationsPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(TRACK_DATA, Track::class.java)!!
        else
            (intent.getParcelableExtra(TRACK_DATA) as? Track)!!

        bindMusicService(track.previewUrl, track.artistName, track.trackName)

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

        viewModel.getPlaylistsLiveData().observe(this) { playlists ->
            playlistsAdapter.submitList(playlists)
        }

        viewModel.getBottomSheetState().observe(this) { state ->
            when (state) {
                BottomSheetState.HIDDEN -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_HIDDEN

                BottomSheetState.COLLAPSED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED

                else -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        viewModel.getMediaPlayerState().observe(this) { state ->
            binding.iBtnPlay.setPlaying(state is MediaPlayerState.Playing)
            binding.tvCurrentTrackTime.text = state.progress
        }

        viewModel.getIsFavorite().observe(this) { isFavorite ->
            setFavoriteButton(isFavorite)
        }

        viewModel.getUiMessageState().observe(this) { message ->
            uiMessageHelper.showCustomSnackbar(message)
        }

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
                tvReleaseDateTitle.isVisible = false
                tvReleaseDateValue.isVisible = false
            } else {
                tvReleaseDateValue.text =
                    SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
            }

            tvGenreValue.text = track.primaryGenreName
            tvCountryValue.text = track.country

            Glide.with(this@MediaPlayerActivity)
                .load(track.artworkUrl100.replaceAfterLast('/', getString(R.string.image_name_512)))
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


            iBtnPlay.onToggleClick = {
                viewModel.playbackControl()
            }

            iBtnFavorite.setOnClickListener {
                viewModel.onFavoriteClicked(track)
            }

            iBtnQueue.setOnClickListener {
                viewModel.getPlaylists()
                viewModel.showBottomSheet()
            }

            mbNewPlaylistButton.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AddPlaylistFragment.newInstance())
                    .addToBackStack(null)
                    .commit()

                binding.fragmentContainer.visibility = View.VISIBLE
            }

            overlay.setOnClickListener {
                viewModel.hideBottomSheet()
            }

            iBtnBack.setOnClickListener { finish() }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.stopForeground()
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

    private fun bindMusicService(songUrl: String, artistName: String, trackName: String) {
        val intent = Intent(this, MusicService::class.java).apply {
            putExtra("song_url", songUrl)
            putExtra("artist_name", artistName)
            putExtra("track_name", trackName)
        }

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindMusicService() {
        unbindService(serviceConnection)
    }

    override fun onStop() {
        Log.i("ACTIVITY", "onStop")
        viewModel.startForeground()
        super.onStop()
    }

    override fun onDestroy() {
        unbindMusicService()
        super.onDestroy()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val TRACK_DATA = "TRACK_DATA"

        fun createArgs(track: Track): Bundle = bundleOf(TRACK_DATA to track)

    }

}