package com.naveed.mymusicapp.features.player.domain

import com.naveed.mymusicapp.core.data.api.MusicRepository
import com.naveed.mymusicapp.core.data.model.Song
import com.naveed.mymusicapp.ext.toException
import com.naveed.mymusicapp.features.player.domain.uimodel.PartialMusicPlayerUiState

class GetSongForId(
    private val musicRepository: MusicRepository
) {

    suspend operator fun invoke(songId: Int): Result<PartialMusicPlayerUiState> {
        val song = musicRepository.getSongById(id = songId).getOrNull()
        return if (song != null) {
            val state = song.toPartialMusicPLayerUiState()
            Result.success(state)
        } else {
            Result.failure("Unable to retreive song".toException())
        }
    }

    private fun Song.toPartialMusicPLayerUiState(): PartialMusicPlayerUiState =
        PartialMusicPlayerUiState(
            songId = id,
            title = title,
            artist = artist,
            imagePath = imagePath,
            data = path
        )
}