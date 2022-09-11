package com.naveed.mymusicapp.features.player

sealed interface PartialMusicPlayerSideEffect {

    object PlaySong: PartialMusicPlayerSideEffect

    object PauseSong: PartialMusicPlayerSideEffect
}