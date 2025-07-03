package com.example.playlistmaker.presentation.ui.library.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist

class PlaylistAdapter: ListAdapter<Playlist, PlaylistViewHolder>(PlaylistDiffCallback()) {

    var onItemClickListener: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_grid_view_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlsit = getItem(position)
        holder.bind(playlsit)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(playlsit)
        }
    }

}