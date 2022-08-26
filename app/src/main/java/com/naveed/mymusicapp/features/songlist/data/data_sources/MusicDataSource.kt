package com.naveed.mymusicapp.features.songlist.data.data_sources

import com.naveed.mymusicapp.features.songlist.data.model.Song

interface MusicDataSource {

    /**
     * Retrieves a list of songs present on the device for the library
     */
    suspend fun getSongs() : Result<List<Song>>
}