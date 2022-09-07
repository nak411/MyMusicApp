package com.naveed.mymusicapp.core.data.api

import com.naveed.mymusicapp.core.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.core.data.model.Song

class MusicRepositoryImpl(
    private val localDataSource: MusicDataSource
): MusicRepository {

    override suspend fun getSongs(): Result<List<Song>> {
        return localDataSource.getSongs()
    }

    override suspend fun getSongById(id: Int): Result<Song> {
        return localDataSource.getSongById(id = id)
    }
}