package com.naveed.mymusicapp.server.domain.usecases

import com.naveed.mymusicapp.core.data.api.MusicRepository
import timber.log.Timber

class SaveCurrentlyPlayingSong(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(songId: String) {
        val song = musicRepository.getSongById(songId).getOrNull()
        if (song != null) {
            musicRepository.saveCurrentlyPlaying(song)
        } else {
            // This can be handled better
            Timber.e("Unable to find song for id: $songId")
        }
    }
}