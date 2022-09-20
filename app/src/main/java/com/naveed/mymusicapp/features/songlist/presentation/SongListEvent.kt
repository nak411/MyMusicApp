package com.naveed.mymusicapp.features.songlist.presentation

sealed interface SongListEvent {

    object LoadSongs : SongListEvent
    data class SelectedSong(val index: Int) : SongListEvent
}