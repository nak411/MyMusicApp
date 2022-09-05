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
    private val _uiState: MutableStateFlow<SongListUiState> = MutableStateFlow(SongListUiState())

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<SongListUiState> = _uiState

    private var prevSelectedIndex: Int = -1

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
        if (index == prevSelectedIndex) {
            // User selected the same song that was previously selected
            return
        }

        _uiState.update { currentState ->
            // Calling toMutable list here will create a new list on each call.
            val songs = currentState.songs.toMutableList()
            // Set previously selected back to normal
            if (prevSelectedIndex != -1) {
                val prevSelectedSong = songs[prevSelectedIndex].copy(isSelected = false)
                songs[prevSelectedIndex] = prevSelectedSong
            }
            // Select the new song
            val updatedSong = songs[index].copy(isSelected = true)
            songs[index] = updatedSong
            prevSelectedIndex = index
            val newState = currentState.copy(songs = songs)
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