package com.example.playlistmaker.ui.search.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.util.Utils

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val artistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val trackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    private val trackLogo: ImageView = itemView.findViewById(R.id.ivTrackLogo)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTimeMillis
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .fitCenter()
            .placeholder(R.drawable.track_image_placeholder)
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(2f, itemView.context)))
            .into(trackLogo)


    }


}