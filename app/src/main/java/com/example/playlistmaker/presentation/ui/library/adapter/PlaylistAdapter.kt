package com.example.playlistmaker.presentation.ui.library.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist

class PlaylistAdapter(private val playlist: List<Playlist>) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    var onItemClickListener: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_grid_view_item, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlist[position])
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(playlist[position])
        }
    }

}