package com.naveed.mymusicapp.features.player

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naveed.mymusicapp.R
import com.naveed.mymusicapp.databinding.FragmentPartialMusicPlayerBinding
import com.naveed.mymusicapp.features.player.domain.uimodel.PartialMusicPlayerUiState
import com.naveed.mymusicapp.server.MusicPlaybackService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PartialMusicPlayerFragment : Fragment() {

    private var _binding: FragmentPartialMusicPlayerBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding: FragmentPartialMusicPlayerBinding get() = _binding!!

    // Since the music player is present everywhere, it is scoped to the activity
    // view model
    private val viewModel: PartialMusicPlayerViewModel by activityViewModels()

    // Media client
    private lateinit var mediaBrowser: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat
    private val controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Timber.d("Metadata changed")
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Timber.d("Playback changed")
        }
    }

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            // Get session token
            val token = mediaBrowser.sessionToken
            // Create a controller for controlling media service
            val mediaController = MediaControllerCompat(requireContext(), token)
            // Save the controller
            MediaControllerCompat.setMediaController(requireActivity(), mediaController)
            // Setup transport controls
            buildTransportControls()
        }

        override fun onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
            Timber.e("Media service crashed")
        }

        override fun onConnectionFailed() {
            // The Service has refused our connection
            Timber.e("Media service connection failed")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeState()
        observeSideEffect()
        setupMediaClient()
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

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(requireActivity())
            ?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupMediaClient() {
        mediaBrowser = MediaBrowserCompat(
            requireContext(),
            ComponentName(requireContext(), MusicPlaybackService::class.java),
            connectionCallback,
            null
        )
    }

    private fun buildTransportControls() {
        mediaController = MediaControllerCompat.getMediaController(requireActivity())
        // Display initial state
        val description = mediaController.metadata.description
        val playbackState = mediaController.playbackState
        if (description != null && playbackState != null) {
            val isPlaying = playbackState.playbackState == PlaybackStateCompat.STATE_PLAYING
            val event = PartialMusicPlayerEvent.LoadState(
                title = description.title.toString(),
                artist = description.subtitle.toString(),
                thumbnail = description.iconUri.toString(),
                isPlaying = isPlaying,
                playPauseIcon = if (isPlaying) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24
            )
            sendEvent(event)
        }
        // Register callback to stay in sync
        mediaController.registerCallback(controllerCallback)
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

    private fun observeSideEffect() {
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
    private fun handleSideEffect(sideEffect: PartialMusicPlayerSideEffect) {
        when (sideEffect) {
            PartialMusicPlayerSideEffect.PauseSong -> pauseSong()
            PartialMusicPlayerSideEffect.PlaySong -> playSong()
        }
    }

    private fun playSong() {
        val playBackState = mediaController.playbackState.state
        if (playBackState == PlaybackStateCompat.STATE_PLAYING) {
            Timber.w("Called play song when state was already playing")
            mediaController.transportControls.pause()
        } else {
            mediaController.transportControls.play()
        }
    }

    private fun pauseSong() {
        val playBackState = mediaController.playbackState.state
        if (playBackState == PlaybackStateCompat.STATE_PLAYING) {
            mediaController.transportControls.pause()
        } else {
            Timber.w("Called pause song when state was already paused")
            mediaController.transportControls.play()
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
            binding.root.isVisible = state.showMusicPlayer
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