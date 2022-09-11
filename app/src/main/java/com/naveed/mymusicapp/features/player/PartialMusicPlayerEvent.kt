package com.naveed.mymusicapp.features.player

sealed interface PartialMusicPlayerEvent {

    data class PlaySong(val songId: String): PartialMusicPlayerEvent

    object ClickedPausePlay: PartialMusicPlayerEvent

    data class LoadState(
        val title: String,
        val artist: String,
        val thumbnail: String,
        val isPlaying: Boolean,
        val playPauseIcon: Int
        ) : PartialMusicPlayerEvent

}