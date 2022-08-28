package com.naveed.mymusicapp.features.songlist.domain.uimodel

import com.naveed.mymusicapp.features.songlist.data.model.Song


data class SongListUiState(
    val songs: List<Song> = emptyList()
)