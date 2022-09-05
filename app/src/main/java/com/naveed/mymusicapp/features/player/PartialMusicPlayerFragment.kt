package com.naveed.mymusicapp.features.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naveed.mymusicapp.databinding.FragmentPartialMusicPlayerBinding
import com.naveed.mymusicapp.features.domain.uimodel.PartialMusicPlayerUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartialMusicPlayerFragment : Fragment() {

    private var _binding: FragmentPartialMusicPlayerBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding: FragmentPartialMusicPlayerBinding get() = _binding!!

    // Since the music player is present everywhere, it is scoped to the activity
    // view model
    private val viewModel: PartialMusicPlayerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPartialMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPlayPause.setOnClickListener {
            sendEvent(PartialMusicPlayerEvent.ClickedPausePlay)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
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
    }

    /**
     * Entry point.  All state updates enter through this function
     */
    private fun updateUi(state: PartialMusicPlayerUiState) {
        with(binding) {
            tvTitle.text = state.title
            tvArtist.text = state.artist
            btnPlayPause.setImageResource(state.playPauseIcon)
        }
    }

    /**
     * Exit Point
     * All events to the view model are sent through this function.  This is the
     * exit point for this fragment
     */
    private fun sendEvent(event: PartialMusicPlayerEvent) {
        viewModel.onEvent(event)
    }
}