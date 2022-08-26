package com.naveed.mymusicapp.features.songlist.data.data_sources.local

import android.content.Context
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import androidx.core.os.EnvironmentCompat
import com.naveed.mymusicapp.features.songlist.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.features.songlist.data.model.Song
import timber.log.Timber

class DeviceStorageMusicDataSource(
    private val context: Context
) : MusicDataSource {

    override suspend fun getSongs(): Result<List<Song>> {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.ARTIST)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} !=0"

        context.contentResolver.query(
            uri,
            projection,
            selection,
            null,
            null
        )?.use { cursor ->
            val artistColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            Timber.d("${cursor.count}")
            while (cursor.moveToNext()) {
                val artistName = cursor.getString(artistColumnIndex)
                Timber.d("/// FOUND ARTIST WITH NAME: $artistName")
            }
        }
        return Result.success(emptyList())
    }
}