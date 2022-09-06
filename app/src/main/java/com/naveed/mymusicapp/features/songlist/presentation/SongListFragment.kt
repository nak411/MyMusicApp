package com.naveed.mymusicapp.features.songlist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.naveed.mymusicapp.core.presentation.delegates.StoragePermissionHandler
import com.naveed.mymusicapp.core.presentation.delegates.StoragePermissionHandlerImpl
import com.naveed.mymusicapp.databinding.FragmentSongListBinding
import com.naveed.mymusicapp.features.player.PartialMusicPlayerEvent
import com.naveed.mymusicapp.features.player.PartialMusicPlayerViewModel
import com.naveed.mymusicapp.features.songlist.domain.uimodel.SongListUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Contains the UI for displaying the list of songs
 */
@AndroidEntryPoint
class SongListFragment : Fragment(), StoragePermissionHandler by StoragePermissionHandlerImpl() {

    private var _binding: FragmentSongListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: SongListViewModel by viewModels()

    // Get player view model for controlling what is playing
    private val playerViewModel: PartialMusicPlayerViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerStoragePermissionLifecycleOwner(
            this,
            onGranted = { sendEvent(SongListEvent.LoadSongs) },
            onDenied = { Timber.e("Failed to get permissions") }
        )
        observeState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.apply {
            adapter = SongListAdapter { index ->
                Timber.d("/// Clicked item: $index")
                sendEvent(SongListEvent.SelectedSong(index = index))
            }
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeState() {
        // Start a coroutine in the lifecycle scope
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Trigger the flow and start listening for values.
                // Note that this happens when lifecycle is STARTED and stops
                // collecting when the lifecycle is STOPPED
                viewModel.uiState.collect { uiState ->
                    updateUi(uiState)
                }
            }
        }
        // Start a coroutine for collecting side effects
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collectLatest { sideEffect ->
                    handleSideEffect(sideEffect)
                }
            }
        }
    }

    /**
     * Entry point for side effects.  All one time events are received here
     */
    private fun handleSideEffect(sideEffect: SongListSideEffect) {
        when (sideEffect) {
            is SongListSideEffect.PlaySong -> playerViewModel.onEvent(
                PartialMusicPlayerEvent.PlaySong(
                    sideEffect.songId
                )
            )
        }
    }

    /**
     * Entry point.  All state updates enter through this function
     */
    private fun updateUi(state: SongListUiState) {
        Timber.d("/// State is: $state")
        val adapter = binding.recyclerView.adapter as SongListAdapter
        adapter.submitList(state.songs)
    }

    /**
     * Exit Point
     * All events to the view model are sent through this function.  This is the
     * exit point for this fragment
     */
    private fun sendEvent(event: SongListEvent) {
        viewModel.onEvent(event)
    }

}