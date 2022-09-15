package com.naveed.mymusicapp.features.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveed.mymusicapp.di.IoDispatcher
import com.naveed.mymusicapp.features.common.domain.MusicServiceClientUseCases
import com.naveed.mymusicapp.features.player.domain.MusicPlayerUseCases
import com.naveed.mymusicapp.features.player.domain.uimodel.PartialMusicPlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartialMusicPlayerViewModel @Inject constructor(
    private val musicPlayerUseCases: MusicPlayerUseCases,
    private val musicServiceClientUseCases: MusicServiceClientUseCases,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _uiState: MutableStateFlow<PartialMusicPlayerUiState> =
        MutableStateFlow(PartialMusicPlayerUiState())
    val uiState: StateFlow<PartialMusicPlayerUiState> = _uiState

    init {
        bindCurrentlyPlayingState()
    }

    /**
     * This is the entry point into the view model and only public facing function
     */
    fun onEvent(event: PartialMusicPlayerEvent) {
        when (event) {
            is PartialMusicPlayerEvent.PlaySong -> playNewSong(event.songId)
            PartialMusicPlayerEvent.ClickedPausePlay -> togglePlay()
        }
    }

    private fun bindCurrentlyPlayingState() {
        viewModelScope.launch {
            musicServiceClientUseCases.observeServiceState().collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun togglePlay() {
        viewModelScope.launch {
            musicServiceClientUseCases.togglePausePlay()
        }

    }

    private fun playNewSong(songId: String) {
        viewModelScope.launch(ioDispatcher) {
            musicPlayerUseCases.getSongById(songId = songId)
                .onSuccess { song ->
                    musicServiceClientUseCases.playSong(songId, song.path)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceClientUseCases.unsubscribeMediaBrowser()
    }
}