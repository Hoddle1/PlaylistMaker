package com.example.playlistmaker.ui.library.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.util.Utils

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.ivCover)
    private val name: TextView = itemView.findViewById(R.id.tvName)
    private val tracksCount: TextView = itemView.findViewById(R.id.tvTracksCount)

    fun bind(model: Playlist) {
        name.text = model.name
        val tracksCountText = "${model.tracksCount} ${formatTracksCount(model.tracksCount)}"
        tracksCount.text = tracksCountText
        Glide.with(itemView)
            .load(model.coverImagePath)
            .fitCenter()
            .placeholder(R.drawable.track_image_placeholder)
            .centerCrop()
            .transform(RoundedCorners(Utils.dpToPx(8f, itemView.context)))
            .into(cover)


    }

    private fun formatTracksCount(count: Int): String {
        return itemView.context.resources.getQuantityString(
            R.plurals.tracks,
            count,
            count
        )
    }
}