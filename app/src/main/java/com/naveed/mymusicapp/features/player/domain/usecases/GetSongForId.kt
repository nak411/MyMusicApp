package com.naveed.mymusicapp.features.player.domain.usecases

import com.naveed.mymusicapp.core.data.api.MusicRepository
import com.naveed.mymusicapp.core.data.model.Song
import com.naveed.mymusicapp.ext.toException
import com.naveed.mymusicapp.features.player.domain.uimodel.PartialMusicPlayerUiState

class GetSongForId(
    private val musicRepository: MusicRepository
) {

    suspend operator fun invoke(songId: String): Result<Song> {
        return musicRepository.getSongById(id = songId)
    }
}