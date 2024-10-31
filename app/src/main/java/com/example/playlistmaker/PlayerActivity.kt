package com.example.playlistmaker

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
import com.example.playlistmaker.Utils.convertMillisToTime
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private var playerState = STATE_DEFAULT

    private var mediaPlayer = MediaPlayer()

    private var mainThreadHandler: Handler? = null

    private var timerRunnable: Runnable? = null

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 500L
        private const val INITIAL_TIME_TIMER = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainThreadHandler = Handler(Looper.getMainLooper())

        enableEdgeToEdge()

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener { finish() }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(Constants.TRACK_DATA, Track::class.java)!!
        else
            (intent.getParcelableExtra(Constants.TRACK_DATA) as? Track)!!

        preparePlayer(track.previewUrl)

        with(binding) {
            textView.text = track.trackName
            artistName.text = track.artistName
            durationValue.text = convertMillisToTime(track.trackTimeMillis)
            if (track.collectionName == null) {
                collectionNameValue.isVisible = false
                collectionNameTitle.isVisible = false
            } else {
                collectionNameValue.text = track.collectionName
            }

            releaseDateValue.text =
                SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)

            genreValue.text = track.primaryGenreName
            countryValue.text = track.country

            Glide.with(this@PlayerActivity)
                .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
                .fitCenter()
                .placeholder(R.drawable.track_image_placeholder)
                .centerCrop()
                .transform(RoundedCorners(Utils.dpToPx(8f, this@PlayerActivity.applicationContext)))
                .into(cover)


            play.setOnClickListener {
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
        mediaPlayer.release()
    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            binding.play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.play.setImageResource(R.drawable.play_button)
            binding.currentTrackTime.text = convertMillisToTime(INITIAL_TIME_TIMER)
            stopTimer()
            playerState = STATE_PREPARED

        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.play.setImageResource(R.drawable.stop_button)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.play.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
                stopTimer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                startTimer()
            }
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
                if (playerState == STATE_PLAYING) {
                    binding.currentTrackTime.text = convertMillisToTime(mediaPlayer.currentPosition)
                    mainThreadHandler?.postDelayed(this, DELAY)
                }

            }
        }
    }

}