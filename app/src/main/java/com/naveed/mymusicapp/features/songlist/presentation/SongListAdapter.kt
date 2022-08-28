package com.naveed.mymusicapp.features.songlist.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.naveed.mymusicapp.databinding.RowSongBinding
import com.naveed.mymusicapp.features.songlist.data.model.Song

class SongListAdapter(
    private val data: List<Song>
) : RecyclerView.Adapter<SongListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size


    class ViewHolder(private val binding: RowSongBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            with(binding) {
                tvTitle.text = song.title
                tvArtist.text = song.artist
                // TODO LOAD IMAGE WITH GLIDE
            }
        }
    }
}