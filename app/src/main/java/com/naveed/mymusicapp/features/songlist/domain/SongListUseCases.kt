package com.naveed.mymusicapp.features.songlist.domain

import com.naveed.mymusicapp.features.songlist.domain.usecases.LoadSongs

class SongListUseCases(
    val loadSongs: LoadSongs
)