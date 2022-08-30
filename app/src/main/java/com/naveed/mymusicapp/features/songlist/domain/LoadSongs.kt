package com.naveed.mymusicapp.features.songlist.domain

import com.naveed.mymusicapp.features.songlist.data.api.MusicRepository
import com.naveed.mymusicapp.features.songlist.data.model.Song
import com.naveed.mymusicapp.features.songlist.domain.uimodel.SongListUiState
import com.naveed.mymusicapp.features.songlist.domain.uimodel.UiSong

class LoadSongs(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): Result<SongListUiState> {
        val songList = musicRepository.getSongs().getOrNull()
        return if (songList != null) {
            val uiSongList = songList.map { it.toUiSong() }
            val songs = SongListUiState(songs = uiSongList)
            Result.success(songs)
        } else {
            Result.failure(Exception("Failed to retrieve songs"))
        }
    }

    private fun Song.toUiSong() : UiSong =
        UiSong(
            id = id,
            title = title,
            artist = artist,
            imagePath = imagePath,
            path = path,
            isSelected = false
        )

}