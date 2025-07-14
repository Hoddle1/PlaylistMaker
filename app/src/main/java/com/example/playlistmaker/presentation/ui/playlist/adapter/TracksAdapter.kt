package com.example.playlistmaker.presentation.ui.playlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.entity.Track

class TracksAdapter: ListAdapter<Track, TracksViewHolder>(TracksDiffCallback()) {

    var onItemClickListener: ((Track) -> Unit)? = null
    var onItemLongClickListener: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_list_view_item, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = getItem(position)

        holder.bind(track)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(track)
        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.invoke(track)
            true
        }
    }

}