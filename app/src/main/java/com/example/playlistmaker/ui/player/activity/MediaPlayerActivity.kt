package com.example.playlistmaker.ui.player.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.entity.Track
import com.example.playlistmaker.ui.player.view_model.MediaPlayerViewModel
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
            binding.iBtnPlay.setImageResource(state.imageResource)
            binding.tvCurrentTrackTime.text = state.progress
        }

        viewModel.getIsFavorite().observe(this) { isFavorite ->
            setFavoriteButton(isFavorite)
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(TRACK_DATA, Track::class.java)!!
        else
            (intent.getParcelableExtra(TRACK_DATA) as? Track)!!

        viewModel.preparePlayer(track.previewUrl)

        setFavoriteButton(track.isFavorite)

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
        private const val TRACK_DATA = "TRACK_DATA"

        fun createArgs(track: Track): Bundle = bundleOf(TRACK_DATA to track)
    }


}