package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Utils.convertMillisToTime

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)
    private val trackLogo: ImageView = itemView.findViewById(R.id.track_logo)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = convertMillisToTime(model.trackTimeMillis)
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .fitCenter()
            .placeholder(R.drawable.track_image_placeholder)
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(2f, itemView.context)))
            .into(trackLogo)


    }


}