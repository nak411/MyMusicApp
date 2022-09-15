package com.naveed.mymusicapp.features.common.domain.usecases

import android.support.v4.media.MediaBrowserCompat
import com.naveed.mymusicapp.core.data.model.Song
import com.naveed.mymusicapp.ext.toException
import com.naveed.mymusicapp.server.MusicServiceConnection
import timber.log.Timber

class GetCurrentlyPlaying(
    private val musicServiceConnection: MusicServiceConnection
) {

    suspend operator fun invoke(): Result<Song> {
        val songList = musicServiceConnection
            .subscribe(parentId = MusicServiceConnection.RECENT_SONG)
        return if (songList.isNotEmpty() && songList.size == 1) {
            // There should only be one recent song that is playing
            Result.success(songList.first().toSong())
        } else {
            Result.failure("No recent song found".toException())
        }
    }

    private fun MediaBrowserCompat.MediaItem.toSong(): Song =
        Song(
            id = description.mediaId!!,
            title = description.title.toString(),
            artist = description.subtitle.toString(),
            imagePath = description.iconUri.toString(),
            path = description.mediaUri.toString()
        )
}