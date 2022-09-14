package com.naveed.mymusicapp.server.domain

import com.naveed.mymusicapp.server.domain.usecases.GetMediaItems
import com.naveed.mymusicapp.server.domain.usecases.SaveCurrentlyPlayingSong

class MusicServiceUseCases(
    val getMediaItems: GetMediaItems,
    val saveCurrentlyPlayingSong: SaveCurrentlyPlayingSong
)