package com.naveed.mymusicapp.server.domain

import com.naveed.mymusicapp.server.domain.usecases.GetMediaItems
import com.naveed.mymusicapp.server.domain.usecases.PlayPauseSong

class MusicServiceUseCases(
    val getMediaItems: GetMediaItems,
    val playPauseSong: PlayPauseSong
)