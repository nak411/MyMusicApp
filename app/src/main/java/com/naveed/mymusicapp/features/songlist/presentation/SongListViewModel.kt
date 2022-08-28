package com.naveed.mymusicapp.features.songlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveed.mymusicapp.di.IoDispatcher
import com.naveed.mymusicapp.features.songlist.SongListUiState
import com.naveed.mymusicapp.features.songlist.data.api.MusicRepository
import com.naveed.mymusicapp.features.songlist.domain.SongListUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        }
    }

    private fun fetchSongs() {
        Timber.d("/// Calling fetch songs")
        viewModelScope.launch(ioDispatcher) {
            songListUseCases.loadSongs()
                .onSuccess { songs ->
                    Timber.d("/// Songs: $songs")
                }
                .onFailure {
                    Timber.e("Failed retrieve songs", it)
                }
        }
    }
}