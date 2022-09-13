package com.naveed.mymusicapp.server.domain.usecases

import android.support.v4.media.session.PlaybackStateCompat
import com.naveed.mymusicapp.server.MusicServiceConnection

/**
 * Plays or pauses the current song depending on the song state
 */
class PlayPauseSong(
    private val musicServiceConnection: MusicServiceConnection
) {

    operator fun invoke() {
        val playBackState = musicServiceConnection.playbackState.value
        val controls = musicServiceConnection.transportControls
        if (playBackState.playbackState == PlaybackStateCompat.STATE_PLAYING) {
            controls.pause()
        } else {
            controls.play()
        }
    }
}