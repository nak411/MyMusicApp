package com.naveed.mymusicapp.features.songlist.presentation

sealed interface SongListEvent {

    object LoadSongs : SongListEvent
}