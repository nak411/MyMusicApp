package com.naveed.mymusicapp.features.songlist.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveed.mymusicapp.di.IoDispatcher
import com.naveed.mymusicapp.features.songlist.data.model.Song
import com.naveed.mymusicapp.features.songlist.domain.uimodel.SongListUiState
import com.naveed.mymusicapp.features.songlist.domain.SongListUseCases
import com.naveed.mymusicapp.features.songlist.domain.uimodel.UiSong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val songListUseCases: SongListUseCases,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow(SongListUiState())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<SongListUiState> = _uiState

    /**
     * This is the entry point into the view model and only public facing function
     */
    fun onEvent(event: SongListEvent) {
        when (event) {
            SongListEvent.LoadSongs -> fetchSongs()
            is SongListEvent.SelectedSong -> updateCurrent(event.index)
        }
    }

    private fun updateCurrent(index: Int) {
        // Calling toMutable list here will create a new list on each call. Instead we use a backing
        // mutable list to modify the same list
        _uiState.update { currentState ->
            Timber.d("//// $currentState")
            val songs = currentState.songs.toMutableList()
            val updatedSong = songs[index].copy(isSelected = true)
            songs[index] = updatedSong
            val newState = currentState.copy(songs = songs)
            Timber.d("//// new state: $newState")
            newState
        }
    }

    private fun fetchSongs() {
        viewModelScope.launch(ioDispatcher) {
            songListUseCases.loadSongs()
                .onSuccess { songs ->
                    _uiState.value = songs
                }
                .onFailure {
                    Timber.e("Failed retrieve songs", it)
                }
        }
    }
}