package com.naveed.mymusicapp.features.songlist.domain.usecases

import android.support.v4.media.MediaBrowserCompat
import com.naveed.mymusicapp.features.common.domain.MusicServiceClientUseCases
import com.naveed.mymusicapp.features.songlist.domain.uimodel.SongListUiState
import com.naveed.mymusicapp.features.songlist.domain.uimodel.UiSong
import com.naveed.mymusicapp.server.MusicServiceConnection
import timber.log.Timber


class LoadSongs(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicServiceClientUseCases: MusicServiceClientUseCases
) {
    suspend operator fun invoke(): Result<SongListUiState> {
      //  val currentlyPLaying = musicServiceClientUseCases.getCurrentlyPlaying().getOrNull()
        val songList = musicServiceConnection.subscribe()
        return if (songList.isNotEmpty()) {
            val uiSongList = songList.map { it.toUiSong() }
            val songs = SongListUiState(songs = uiSongList)
            Result.success(songs)
        } else {
            Result.failure(Exception("Failed to retrieve songs"))
        }
    }

    private fun MediaBrowserCompat.MediaItem.toUiSong(): UiSong =
        UiSong(
            id = description.mediaId!!,
            title = description.title.toString(),
            artist = description.subtitle.toString(),
            imagePath = description.iconUri.toString(),
            path = description.mediaUri.toString(),
            isSelected = false
        )

}