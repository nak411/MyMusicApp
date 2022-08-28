package com.naveed.mymusicapp.features.songlist

import com.naveed.mymusicapp.features.songlist.data.model.Song


data class SongListUiState(
    val songs: List<Song> = emptyList()
)