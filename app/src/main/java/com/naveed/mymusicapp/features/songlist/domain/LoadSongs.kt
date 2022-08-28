package com.naveed.mymusicapp.features.songlist.domain

import com.naveed.mymusicapp.features.songlist.SongListUiState
import com.naveed.mymusicapp.features.songlist.data.api.MusicRepository

class LoadSongs(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): Result<SongListUiState> {
        val songList = musicRepository.getSongs().getOrNull()
        return if (songList != null) {
            val songs = SongListUiState(songs = songList)
            Result.success(songs)
        } else {
            Result.failure(Exception("Failed to retrieve songs"))
        }
    }
}