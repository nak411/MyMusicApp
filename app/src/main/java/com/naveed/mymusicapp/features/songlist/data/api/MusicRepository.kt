package com.naveed.mymusicapp.features.songlist.data.api

import com.naveed.mymusicapp.features.songlist.data.model.Song

interface MusicRepository {

    /**
     * Retrieves a list of songs present on the device for the library
     */
    suspend fun getSongs() : Result<List<Song>>
}