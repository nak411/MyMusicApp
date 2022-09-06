package com.naveed.mymusicapp.features.songlist.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naveed.mymusicapp.R
import com.naveed.mymusicapp.databinding.RowSongBinding
import com.naveed.mymusicapp.ext.getPrimaryColor
import com.naveed.mymusicapp.ext.getTextColor
import com.naveed.mymusicapp.core.data.model.Song
import com.naveed.mymusicapp.features.songlist.domain.uimodel.UiSong

class SongListAdapter(
    private val onItemClicked: (Int) -> Unit
) : ListAdapter<UiSong, SongListAdapter.ViewHolder>(SongDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: RowSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: UiSong) {
            with(binding) {
                tvTitle.text = song.title
                tvArtist.text = song.artist
                root.setOnClickListener {
                    onItemClicked(adapterPosition)
                }
                val selectedColor = root.context.getPrimaryColor()
                val normalColor = root.context.getTextColor()

                // Set color for selected view
                if (song.isSelected) {
                    tvTitle.setTextColor(selectedColor)
                    ImageViewCompat.setImageTintList(
                        ivThumbnail,
                        ColorStateList.valueOf(selectedColor)
                    )
                } else {
                    tvTitle.setTextColor(normalColor)
                    ImageViewCompat.setImageTintList(
                        ivThumbnail,
                        ColorStateList.valueOf(normalColor)
                    )
                }

                // TODO LOAD IMAGE WITH GLIDE
            }
        }
    }

    private class SongDiffCallback : DiffUtil.ItemCallback<UiSong>() {

        override fun areItemsTheSame(oldItem: UiSong, newItem: UiSong): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UiSong, newItem: UiSong): Boolean {
            return oldItem == newItem
        }

    }
}