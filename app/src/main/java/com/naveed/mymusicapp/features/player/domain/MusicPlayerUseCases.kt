package com.naveed.mymusicapp.features.player.domain

import com.naveed.mymusicapp.features.player.domain.usecases.GetSongForId

class MusicPlayerUseCases(
    val getSongById: GetSongForId
)