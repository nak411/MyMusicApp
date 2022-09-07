package com.naveed.mymusicapp.core.data.data_sources

import com.naveed.mymusicapp.core.data.model.Song

interface MusicDataSource {

    /**
     * Retrieves a list of songs present on the device for the library
     */
    suspend fun getSongs() : Result<List<Song>>

    /**
     * Retrieves a [Song] using the provided id
     * @param id an integer representing the id of the song to retrieve
     *
     * @return the song for the provided id or an exception if the song was not found
     */
    suspend fun getSongById(id: Int): Result<Song>
}