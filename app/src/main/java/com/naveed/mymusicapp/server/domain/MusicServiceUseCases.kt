package com.naveed.mymusicapp.server.domain

import com.naveed.mymusicapp.server.domain.usecases.GetMediaItems

class MusicServiceUseCases(
    val getMediaItems: GetMediaItems
)