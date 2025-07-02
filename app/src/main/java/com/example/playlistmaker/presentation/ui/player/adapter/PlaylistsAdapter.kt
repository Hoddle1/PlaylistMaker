package com.example.playlistmaker.presentation.ui.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Playlist


class PlaylistsAdapter(private val playlists: List<Playlist>) : RecyclerView.Adapter<PlaylistsViewHolder>() {

    var onItemClickListener: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_list_view_item, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(playlists[position])
        }
    }

}