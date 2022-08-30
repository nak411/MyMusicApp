package com.naveed.mymusicapp.features.songlist.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naveed.mymusicapp.databinding.RowSongBinding
import com.naveed.mymusicapp.features.songlist.data.model.Song

class SongListAdapter(
) : ListAdapter<Song, SongListAdapter.ViewHolder>(SongDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: RowSongBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            with(binding) {
                tvTitle.text = song.title
                tvArtist.text = song.artist
                // TODO LOAD IMAGE WITH GLIDE
            }
        }
    }

    private class SongDiffCallback: DiffUtil.ItemCallback<Song>() {

        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

    }
}