package com.naveed.mymusicapp.server.domain.usecases

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.naveed.mymusicapp.core.data.api.MusicRepository
import com.naveed.mymusicapp.core.data.model.Song
import com.naveed.mymusicapp.ext.toException

class GetRecentMediaItem(
    private val musicRepository: MusicRepository
) {

    suspend operator fun invoke(): Result<MediaBrowserCompat.MediaItem> {
        val song = musicRepository.getCurrentlyPLaying().getOrNull()
        return if (song != null) {
            val mediaItem = song.toMediaItem()
            Result.success(mediaItem)
        } else {
            Result.failure("Failed to retrieve song".toException())
        }
    }

    private fun Song.toMediaItem(): MediaBrowserCompat.MediaItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(id)
            .setTitle(title)
            .setSubtitle(artist)
            .setIconUri(Uri.parse(imagePath))
            .setMediaUri(Uri.parse(path))
            .build()
        val flag = MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        return MediaBrowserCompat.MediaItem(description, flag)
    }
}