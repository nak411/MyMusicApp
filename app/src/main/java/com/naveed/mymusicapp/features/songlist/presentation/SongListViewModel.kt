package com.naveed.mymusicapp.features.songlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naveed.mymusicapp.di.IoDispatcher
import com.naveed.mymusicapp.features.songlist.data.api.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
 ): ViewModel() {

    fun fetchSongs() {
        viewModelScope.launch(ioDispatcher) {
            musicRepository.getSongs()
        }
    }
}