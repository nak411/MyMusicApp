package com.naveed.mymusicapp.core.data.data_sources.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import androidx.core.os.EnvironmentCompat
import com.naveed.mymusicapp.core.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.core.data.model.Song
import timber.log.Timber

class DeviceStorageMusicDataSource(
    private val context: Context
) : MusicDataSource {

    override suspend fun getSongs(): Result<List<Song>> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Albums.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} !=0"
        val songs = loadSongs(uri = uri, projection = projection, selection = selection)
        return Result.success(songs)
    }

    private fun loadSongs(
        uri: Uri,
        projection: Array<String>,
        selection: String
    ): List<Song> {
        val songs = mutableListOf<Song>()
        context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val songIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val albumColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)
            while (cursor.moveToNext()) {
                val id = cursor.getString(songIdColumn)
                val title = cursor.getString(titleColumnIndex)
                val artistName = cursor.getString(artistColumnIndex)
                val songPath = cursor.getString(dataColumnIndex)
                val albumId = cursor.getLong(albumColumnIndex)
                val albumArt = ContentUris.withAppendedId(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    albumId
                )
                val song = Song(
                    id = id,
                    title = title,
                    artist = artistName,
                    imagePath = albumArt.toString(),
                    path = songPath
                )
                songs.add(song)
            }
        }
        return songs
    }
}