package com.naveed.mymusicapp.features.songlist.presentation

sealed interface SongListSideEffect {

    data class PlaySong(val songId: String): SongListSideEffect

//    object LoadPlayer : SongListSideEffect
}