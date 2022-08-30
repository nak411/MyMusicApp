package com.naveed.mymusicapp.features.songlist.domain.uimodel


data class SongListUiState(
    val songs: List<UiSong> = emptyList()
)

data class UiSong(
    val id: String,
    val title: String,
    val artist: String,
    val imagePath: String,
    val path: String,
    val isSelected: Boolean = false
)