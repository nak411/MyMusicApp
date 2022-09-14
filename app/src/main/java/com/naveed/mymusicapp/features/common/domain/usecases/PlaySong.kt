package com.naveed.mymusicapp.features.common.domain.usecases

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.naveed.mymusicapp.server.MusicServiceConnection
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * Plays the song for the specified song id canceling any currently playing
 * song.  If the currently playing song is the same as the provided id,
 * then the song is restarted
 */
class PlaySong(
    private val musicServiceConnection: MusicServiceConnection
) {

    private var hasObserver: Boolean = false

    suspend operator fun invoke(songId: String, songUri: String) {
        val controls = musicServiceConnection.transportControls
        val arguments = Bundle().apply {
            putString(MusicServiceConnection.SONG_ID, songId)
        }
        // Prepare the uri
        controls.prepareFromUri(Uri.parse(songUri), arguments)
        // Ensure that only one observer is attached per instance of this class to avoid
        // collecting same item multiple times
        if (!hasObserver) {
            hasObserver = true
            musicServiceConnection.playbackState.collect { state ->
                // Attach and observer for media state
                if (state.state == PlaybackStateCompat.STATE_BUFFERING) {
                    Timber.d("//// Calling play")
                    controls.play()
                }
            }
        }
    }
}