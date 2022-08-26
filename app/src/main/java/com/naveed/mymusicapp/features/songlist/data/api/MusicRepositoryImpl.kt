package com.naveed.mymusicapp.features.songlist.data.api

import com.naveed.mymusicapp.features.songlist.data.data_sources.SongDataSource
import com.naveed.mymusicapp.features.songlist.data.model.Song

class MusicRepositoryImpl(
    private val localDataSource: SongDataSource
): MusicRepository {
    
    override suspend fun getSongs(): Result<List<Song>> {
        return localDataSource.getSongs()
    }
}