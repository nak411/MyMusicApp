package com.naveed.mymusicapp.core.data.api

import com.naveed.mymusicapp.core.data.model.Song

interface MusicRepository {

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

    /**
     * Saves the provided song as the currently playing song.  Any existing currently playing
     * song will be replaced by the provided song
     * @param song the song to save as the currently playing song
     */
    suspend fun saveCurrentlyPlaying(song: Song): Result<Boolean>

    /**
     * Retrieves the currently playing song or null if nothing was previously played
     */
    fun getCurrentlyPLaying() : Result<Song>
}