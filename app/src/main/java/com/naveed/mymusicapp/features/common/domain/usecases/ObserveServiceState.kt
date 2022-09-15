package com.naveed.mymusicapp.features.common.domain.usecases

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.naveed.mymusicapp.R
import com.naveed.mymusicapp.features.player.domain.uimodel.PartialMusicPlayerUiState
import com.naveed.mymusicapp.server.MusicServiceConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Provides observable api for monitoring the currently playing song.  The view model
 * can bind to this state to properly update the ui as the song state changes.
 */
class ObserveServiceState(
    musicServiceConnection: MusicServiceConnection
) {

    private val playBackStateFlow = musicServiceConnection.playbackState
    private val recentItemFlow = musicServiceConnection.observeCurrentlyPlaying()

    private val combinedFlow = recentItemFlow.combine(playBackStateFlow) { m, playBackState ->
        val mediaItem = m.firstOrNull()
        val isPlaying = playBackState.state == PlaybackStateCompat.STATE_PLAYING
        // We have a media item we can process
        mediaItem?.toPartialMusicPlayerUiState(isPlaying) ?: PartialMusicPlayerUiState()
    }

    operator fun invoke(): Flow<PartialMusicPlayerUiState> {
        return combinedFlow
    }

    private fun MediaBrowserCompat.MediaItem.toPartialMusicPlayerUiState(
        isPlaying: Boolean
    ): PartialMusicPlayerUiState {
        return with(description) {
            PartialMusicPlayerUiState(
                songId = mediaId!!,
                title = title.toString(),
                artist = subtitle.toString(),
                imagePath = iconUri.toString(),
                data = mediaUri.toString(),
                isPlaying = isPlaying,
                playPauseIcon = if (isPlaying) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24,
                showMusicPlayer = true
            )
        }
    }
}