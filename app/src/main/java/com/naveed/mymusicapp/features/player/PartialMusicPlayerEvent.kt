package com.naveed.mymusicapp.features.player

sealed interface PartialMusicPlayerEvent {

    data class PlaySong(val songId: String): PartialMusicPlayerEvent

    object PauseSong: PartialMusicPlayerEvent

    data class ShowMusicPlayer(val show: Boolean) : PartialMusicPlayerEvent

}