package com.example.playlistmaker.player.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.view_model.MediaPlayerState
import com.example.playlistmaker.player.ui.view_model.MediaPlayerViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale


class MediaPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<MediaPlayerViewModel>()

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

        viewModel.getMediaPlayerState().observe(this) { state ->
            when (state) {
                MediaPlayerState.Playing -> {
                    binding.iBtnPlay.setImageResource(R.drawable.stop_button)
                }

                MediaPlayerState.Default, MediaPlayerState.Paused, MediaPlayerState.Prepared -> {
                    binding.iBtnPlay.setImageResource(R.drawable.play_button)
                }
            }
        }
        viewModel.getCurrentTrackTime().observe(this) { currentTrackTime ->
            binding.tvCurrentTrackTime.text = currentTrackTime
        }

        binding.iBtnBack.setOnClickListener { finish() }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(TRACK_DATA, Track::class.java)!!
        else
            (intent.getParcelableExtra(TRACK_DATA) as? Track)!!

        viewModel.preparePlayer(track.previewUrl)

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

            tvReleaseDateValue.text =
                SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)

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
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopTimer()
        viewModel.releasePlayer()
    }

    companion object {
        private const val TRACK_DATA = "TRACK_DATA"

        fun openPlayer(context: Context, track: Track) {
            val intent = Intent(context, MediaPlayerActivity::class.java)
            intent.putExtra(TRACK_DATA, track)
            context.startActivity(intent)
        }
    }

}