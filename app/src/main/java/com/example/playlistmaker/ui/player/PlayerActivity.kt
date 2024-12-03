package com.example.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Constants
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.Utils
import com.example.playlistmaker.Utils.convertMillisToTime
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.model.Track
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor(MediaPlayer())

    private var mainThreadHandler: Handler? = null

    private var timerRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainThreadHandler = Handler(Looper.getMainLooper())

        enableEdgeToEdge()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.svMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.iBtnBack.setOnClickListener { finish() }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(Constants.TRACK_DATA, Track::class.java)!!
        else
            (intent.getParcelableExtra(Constants.TRACK_DATA) as? Track)!!

        preparePlayer(track.previewUrl)

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

            Glide.with(this@PlayerActivity)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .fitCenter()
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(Utils.dpToPx(8f, this@PlayerActivity.applicationContext)))
                .into(ivCover)


            iBtnPlay.setOnClickListener {
                playbackControl()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        mediaPlayerInteractor.releasePlayer()
    }

    private fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(
            url = url,
            onPrependListener = {
                binding.iBtnPlay.isEnabled = true
                mediaPlayerInteractor.setState(
                    PlayerState(
                        PlayerState.State.PREPARED
                    )
                )
            },
            onCompletionListener = {
                binding.iBtnPlay.setImageResource(R.drawable.play_button)
                binding.tvCurrentTrackTime.text = convertMillisToTime(INITIAL_TIME_TIMER_MILLIS)
                stopTimer()
                mediaPlayerInteractor.setState(
                    PlayerState(
                        PlayerState.State.PREPARED
                    )
                )
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        binding.iBtnPlay.setImageResource(R.drawable.stop_button)
        mediaPlayerInteractor.setState(
            PlayerState(
                PlayerState.State.PLAYING
            )
        )
    }

    private fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        binding.iBtnPlay.setImageResource(R.drawable.play_button)
        mediaPlayerInteractor.setState(
            PlayerState(
                PlayerState.State.PAUSED
            )
        )
    }

    private fun playbackControl() {
        when (mediaPlayerInteractor.getState().state) {
            PlayerState.State.PLAYING -> {
                pausePlayer()
                stopTimer()
            }

            PlayerState.State.PREPARED, PlayerState.State.PAUSED -> {
                startPlayer()
                startTimer()
            }

            else -> {}
        }
    }

    private fun startTimer() {
        timerRunnable = createUpdateTimerTask()
        mainThreadHandler?.post(timerRunnable!!)
    }

    private fun stopTimer() {
        timerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (mediaPlayerInteractor.getState().state == PlayerState.State.PLAYING) {
                    binding.tvCurrentTrackTime.text = mediaPlayerInteractor.getPlayerTime()
                    mainThreadHandler?.postDelayed(this, DELAY_MILLIS)
                }

            }
        }
    }

    companion object {
        private const val DELAY_MILLIS = 500L
        private const val INITIAL_TIME_TIMER_MILLIS = 0
    }

}