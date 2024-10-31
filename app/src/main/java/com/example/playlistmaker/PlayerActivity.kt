package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        }
    }
}