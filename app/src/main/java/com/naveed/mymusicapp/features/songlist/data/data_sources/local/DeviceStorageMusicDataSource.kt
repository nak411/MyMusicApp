package com.naveed.mymusicapp.features.songlist.data.data_sources.local

import com.naveed.mymusicapp.features.songlist.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.features.songlist.data.model.Song

class DeviceStorageMusicDataSource: MusicDataSource {

    override suspend fun getSongs(): Result<List<Song>> {
        TODO("Not yet implemented")
    }
}