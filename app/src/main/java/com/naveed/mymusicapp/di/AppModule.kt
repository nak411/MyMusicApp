package com.naveed.mymusicapp.di

import android.content.Context
import com.naveed.mymusicapp.core.data.api.MusicRepository
import com.naveed.mymusicapp.core.data.api.MusicRepositoryImpl
import com.naveed.mymusicapp.core.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.core.data.data_sources.local.DeviceStorageMusicDataSource
import com.naveed.mymusicapp.features.player.domain.GetSongForId
import com.naveed.mymusicapp.features.player.domain.MusicPlayerUseCases
import com.naveed.mymusicapp.features.songlist.domain.LoadSongs
import com.naveed.mymusicapp.features.songlist.domain.SongListUseCases
import com.naveed.mymusicapp.server.domain.MusicServiceUseCases
import com.naveed.mymusicapp.server.domain.usecases.GetMediaItems
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

    @Provides
    fun provideMusicPlayerUseCases(
        musicRepository: MusicRepository
    ): MusicPlayerUseCases {
        return MusicPlayerUseCases(
            getSongById = GetSongForId(musicRepository = musicRepository)
        )
    }

    @Provides
    fun provideMusicServiceUseCases(
        musicRepository: MusicRepository
    ): MusicServiceUseCases {
        return MusicServiceUseCases(
            getMediaItems = GetMediaItems(musicRepository = musicRepository)
        )
    }
}