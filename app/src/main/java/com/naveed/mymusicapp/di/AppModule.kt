package com.naveed.mymusicapp.di

import android.content.Context
import com.naveed.mymusicapp.features.songlist.data.api.MusicRepository
import com.naveed.mymusicapp.features.songlist.data.api.MusicRepositoryImpl
import com.naveed.mymusicapp.features.songlist.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.features.songlist.data.data_sources.local.DeviceStorageMusicDataSource
import com.naveed.mymusicapp.features.songlist.domain.LoadSongs
import com.naveed.mymusicapp.features.songlist.domain.SongListUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        @Named("musicLocalDataSource") localDataSource: MusicDataSource
    ): MusicRepository {
        return MusicRepositoryImpl(
            localDataSource = localDataSource
        )
    }

    @Provides
    @Singleton
    @Named("musicLocalDataSource")
    fun provideMusicLocalDataSource(
        @ApplicationContext context: Context
    ): MusicDataSource {
        return DeviceStorageMusicDataSource(
            context = context
        )
    }

    @Provides
    fun provideSongListUseCases(
        musicRepository: MusicRepository
    ): SongListUseCases {
        return SongListUseCases(
            loadSongs = LoadSongs(musicRepository = musicRepository)
        )
    }
}