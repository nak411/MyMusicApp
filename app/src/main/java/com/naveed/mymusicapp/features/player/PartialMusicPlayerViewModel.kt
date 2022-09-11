package com.naveed.mymusicapp.features.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveed.mymusicapp.R
import com.naveed.mymusicapp.features.player.domain.MusicPlayerUseCases
import com.naveed.mymusicapp.features.player.domain.uimodel.PartialMusicPlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartialMusicPlayerViewModel @Inject constructor(
    private val musicPlayerUseCases: MusicPlayerUseCases
) : ViewModel() {

    private val _sideEffect: MutableSharedFlow<PartialMusicPlayerSideEffect> = MutableSharedFlow()
    val sideEffect: SharedFlow<PartialMusicPlayerSideEffect> = _sideEffect

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
        emitEffect(PartialMusicPlayerSideEffect.PauseSong)
    }

    private fun playSong(songId: String) {
        viewModelScope.launch {
            musicPlayerUseCases.getSongById(songId = songId.toInt())
                .onSuccess { song ->
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
                    emitEffect(PartialMusicPlayerSideEffect.PlaySong)
                }
        }
    }

    private fun emitEffect(effect: PartialMusicPlayerSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }
}