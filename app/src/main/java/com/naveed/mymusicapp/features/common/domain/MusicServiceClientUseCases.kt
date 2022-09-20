package com.naveed.mymusicapp.features.common.domain

import com.naveed.mymusicapp.features.common.domain.usecases.ObserveServiceState
import com.naveed.mymusicapp.features.common.domain.usecases.PlaySong
import com.naveed.mymusicapp.features.common.domain.usecases.TogglePausePlay
import com.naveed.mymusicapp.features.common.domain.usecases.UnsubscribeMediaBrowser
import com.naveed.mymusicapp.server.MusicPlaybackService
/**
 * Contains use cases for communicating and controlling the [MusicPlaybackService]
 */
class MusicServiceClientUseCases(
    val playSong: PlaySong,
    val unsubscribeMediaBrowser: UnsubscribeMediaBrowser,
    val togglePausePlay: TogglePausePlay,
    val observeServiceState: ObserveServiceState
)