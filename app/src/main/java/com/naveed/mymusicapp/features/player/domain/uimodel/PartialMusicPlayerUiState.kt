package com.naveed.mymusicapp.features.player.domain.uimodel

import com.naveed.mymusicapp.R

data class PartialMusicPlayerUiState(
    val songId: String = "",
    val title: String = "",
    val artist: String = "",
    val imagePath: String = "",
    val data: String = "",
    val isPlaying: Boolean = false,
    val playPauseIcon: Int = R.drawable.ic_baseline_play_arrow_24,
    val showMusicPlayer: Boolean = false
)