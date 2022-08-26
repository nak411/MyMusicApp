package com.naveed.mymusicapp.features.songlist.data.api

import com.naveed.mymusicapp.features.songlist.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.features.songlist.data.model.Song

class MusicRepositoryImpl(
    private val localDataSource: MusicDataSource
): MusicRepository {

    override suspend fun getSongs(): Result<List<Song>> {
        return localDataSource.getSongs()
    }
}