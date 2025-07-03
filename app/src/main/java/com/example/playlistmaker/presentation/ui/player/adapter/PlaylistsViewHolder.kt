package com.example.playlistmaker.presentation.ui.player.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist
import com.example.playlistmaker.presentation.util.Utils

class PlaylistsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val name: TextView = itemView.findViewById(R.id.tvPlaylistName)
    private val tracksCount: TextView = itemView.findViewById(R.id.tvTracksCount)
    private val cover: ImageView = itemView.findViewById(R.id.ivCover)

    fun bind(model: Playlist) {
        name.text = model.name
        tracksCount.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks,
            model.tracksCount,
            model.tracksCount
        )

        Glide.with(itemView)
            .load(model.coverImagePath)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .transform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(
                                Utils.dpToPx(2f, itemView.context)
                            )
                        )
                    )
            )
            .placeholder(R.drawable.track_image_placeholder)
            .into(cover)


    }
}