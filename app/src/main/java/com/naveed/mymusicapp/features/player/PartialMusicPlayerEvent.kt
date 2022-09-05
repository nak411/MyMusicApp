package com.naveed.mymusicapp.features.player

sealed interface PartialMusicPlayerEvent {

    data class PlaySong(val songId: String): PartialMusicPlayerEvent

    object ClickedPausePlay: PartialMusicPlayerEvent

    data class ShowMusicPlayer(val show: Boolean) : PartialMusicPlayerEvent

}