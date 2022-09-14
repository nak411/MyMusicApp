package com.naveed.mymusicapp.di

import android.content.ComponentName
import android.content.Context
import com.naveed.mymusicapp.core.data.api.MusicRepository
import com.naveed.mymusicapp.core.data.api.MusicRepositoryImpl
import com.naveed.mymusicapp.core.data.data_sources.MusicDataSource
import com.naveed.mymusicapp.core.data.data_sources.local.DeviceStorageMusicDataSource
import com.naveed.mymusicapp.features.common.domain.MusicServiceClientUseCases
import com.naveed.mymusicapp.features.common.domain.usecases.PlaySong
import com.naveed.mymusicapp.features.common.domain.usecases.UnsubscribeMediaBrowser
import com.naveed.mymusicapp.features.player.domain.usecases.GetSongForId
import com.naveed.mymusicapp.features.player.domain.MusicPlayerUseCases
import com.naveed.mymusicapp.features.songlist.domain.LoadSongs
import com.naveed.mymusicapp.features.songlist.domain.SongListUseCases
import com.naveed.mymusicapp.server.MusicPlaybackService
import com.naveed.mymusicapp.server.MusicServiceConnection
import com.naveed.mymusicapp.server.domain.MusicServiceUseCases
import com.naveed.mymusicapp.server.domain.usecases.GetMediaItems
import com.naveed.mymusicapp.server.domain.usecases.SaveCurrentlyPlayingSong
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
            context = context,
            preferences = context.getSharedPreferences("my_music_app_prefs", Context.MODE_PRIVATE)
        )
    }

    @Provides
    @Singleton
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context,
        componentName: ComponentName
    ): MusicServiceConnection {
        return MusicServiceConnection(context = context, componentName = componentName)
    }

    @Provides
    fun provideComponentName(
       @ApplicationContext context: Context
    ): ComponentName {
        return ComponentName(context, MusicPlaybackService::class.java)
    }

    @Provides
    fun provideSongListUseCases(
        musicServiceConnection: MusicServiceConnection
    ): SongListUseCases {
        return SongListUseCases(
            loadSongs = LoadSongs(musicServiceConnection = musicServiceConnection)
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
    fun provideMusicClientUseCases(
        musicServiceConnection: MusicServiceConnection
    ) : MusicServiceClientUseCases {
        return MusicServiceClientUseCases(
            playSong = PlaySong(musicServiceConnection = musicServiceConnection),
            unsubscribeMediaBrowser = UnsubscribeMediaBrowser(musicServiceConnection = musicServiceConnection)
        )
    }

    @Provides
    fun provideMusicServiceUseCases(
        musicRepository: MusicRepository
    ): MusicServiceUseCases {
        return MusicServiceUseCases(
            getMediaItems = GetMediaItems(musicRepository = musicRepository),
            saveCurrentlyPlayingSong = SaveCurrentlyPlayingSong(musicRepository = musicRepository)
        )
    }
}