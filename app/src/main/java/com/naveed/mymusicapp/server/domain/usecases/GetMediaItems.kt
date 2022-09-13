package com.naveed.mymusicapp.server.domain.usecases

import android.net.Uri
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.naveed.mymusicapp.core.data.api.MusicRepository
import com.naveed.mymusicapp.core.data.model.Song
import com.naveed.mymusicapp.ext.toException

class GetMediaItems(
    val musicRepository: MusicRepository
) {

    suspend operator fun invoke(): Result<List<MediaBrowserCompat.MediaItem>> {
        val songs = musicRepository.getSongs().getOrNull()
        return if (songs != null) {
            val mediaItems = songs.map { song -> song.toMediaItem() }
            Result.success(mediaItems)
        } else {
            Result.failure("Failed to retrieve songs".toException())
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
        val flag = MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
        return MediaBrowserCompat.MediaItem(description, flag)
    }
}