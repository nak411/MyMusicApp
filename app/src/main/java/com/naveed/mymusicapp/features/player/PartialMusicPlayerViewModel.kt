package com.naveed.mymusicapp.features.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveed.mymusicapp.R
import com.naveed.mymusicapp.di.IoDispatcher
import com.naveed.mymusicapp.di.MainDispatcher
import com.naveed.mymusicapp.features.common.domain.MusicServiceClientUseCases
import com.naveed.mymusicapp.features.player.domain.MusicPlayerUseCases
import com.naveed.mymusicapp.features.player.domain.uimodel.PartialMusicPlayerUiState
import com.naveed.mymusicapp.server.MusicServiceConnection
import com.naveed.mymusicapp.server.domain.MusicServiceUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PartialMusicPlayerViewModel @Inject constructor(
    private val musicPlayerUseCases: MusicPlayerUseCases,
    private val musicServiceClientUseCases: MusicServiceClientUseCases,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _uiState: MutableStateFlow<PartialMusicPlayerUiState> =
        MutableStateFlow(PartialMusicPlayerUiState())
    val uiState: StateFlow<PartialMusicPlayerUiState> = _uiState

    /**
     * This is the entry point into the view model and only public facing function
     */
    fun onEvent(event: PartialMusicPlayerEvent) {
        when (event) {
            is PartialMusicPlayerEvent.PlaySong -> playSong(event.songId)
            PartialMusicPlayerEvent.ClickedPausePlay -> togglePlay()
            is PartialMusicPlayerEvent.LoadState -> loadState(event)
        }
    }

    private fun loadState(event: PartialMusicPlayerEvent.LoadState) {
        _uiState.update { currentState ->
            currentState.copy(
                title = event.title,
                artist = event.artist,
                imagePath = event.thumbnail,
                isPlaying = event.isPlaying,
                playPauseIcon = event.playPauseIcon
            )
        }
    }

    private fun togglePlay() {
        if (uiState.value.isPlaying) pauseSong() else playSong(uiState.value.songId)
    }

    private fun pauseSong() {
        _uiState.update { currentState ->
            currentState.copy(
                isPlaying = false,
                playPauseIcon = R.drawable.ic_baseline_play_arrow_24
            )
        }
    }

    private fun playSong(songId: String) {
        viewModelScope.launch(ioDispatcher) {
            musicPlayerUseCases.getSongById(songId = songId)
                .onSuccess { song ->
                    withContext(mainDispatcher) {
                        musicServiceClientUseCases.playSong(songId = song.id, songUri = song.path)
                        _uiState.update { currentState ->
                            currentState.copy(
                                songId = song.id,
                                title = song.title,
                                artist = song.artist,
                                data = song.path,
                                imagePath = song.imagePath,
                                isPlaying = true,
                                playPauseIcon = R.drawable.ic_baseline_pause_24,
                                showMusicPlayer = true
                            )
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceClientUseCases.unsubscribeMediaBrowser()
    }
}