package com.naveed.mymusicapp.features.common.domain.usecases

import android.support.v4.media.session.PlaybackStateCompat
import com.naveed.mymusicapp.server.MusicServiceConnection

/**
 * This use case assumes that a song is either currently playing or loaded into the player
 */
class TogglePausePlay(
    private val musicServiceConnection: MusicServiceConnection
) {

     operator fun invoke() {
        val controls = musicServiceConnection.transportControls
        val state = musicServiceConnection.playbackState.value
        if (state.state == PlaybackStateCompat.STATE_PLAYING) {
            controls.pause()
        } else {
            controls.play()
        }
    }
}