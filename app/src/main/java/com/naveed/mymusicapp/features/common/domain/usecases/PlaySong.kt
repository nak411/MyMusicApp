package com.naveed.mymusicapp.features.common.domain.usecases

import android.net.Uri
import android.os.Bundle
import com.naveed.mymusicapp.server.MusicServiceConnection

/**
 * Plays the song for the specified song id canceling any currently playing
 * song.  If the currently playing song is the same as the provided id,
 * then the song is restarted
 */
class PlaySong(
    private val musicServiceConnection: MusicServiceConnection
) {

    operator fun invoke(songId: String, songUri: String) {
        val controls = musicServiceConnection.transportControls
        val arguments = Bundle().apply {
            putString(MusicServiceConnection.SONG_ID, songId)
        }
        controls.playFromUri(Uri.parse(songUri), arguments)
    }
}