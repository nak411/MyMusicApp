package com.naveed.mymusicapp.server

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.naveed.mymusicapp.ext.observe
import com.naveed.mymusicapp.ext.subscribe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber


class MusicServiceConnection(
    context: Context,
    componentName: ComponentName
) {

    /**
     * Exposed the consumer.  This allows the consumer to control the service
     */
    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    /**
     * Immutable state flow of playback state that the consumer can use for monitoring state
     */
    val playbackState: StateFlow<PlaybackStateCompat> get() = _playbackState
    private val _playbackState: MutableStateFlow<PlaybackStateCompat> = MutableStateFlow(
        EMPTY_PLAYBACK_STATE
    )

    private val connectionCallback: MediaBrowserCompat.ConnectionCallback =
        MediaBrowserConnectionCallback(context = context)
    private val controllerCallback: MediaControllerCompat.Callback = MediaControllerCallback()

    // Setup a media browser which is used for connecting with the service and establishing a session
    private val mediaBrowser: MediaBrowserCompat = MediaBrowserCompat(
        context,
        componentName,
        connectionCallback,
        null
    ).apply { connect() }

    /**
     * Used for controlling and issuing commands to the service.  This is initialized after
     * a successful connection is established in [MediaBrowserConnectionCallback#onConnected]
     */
    private lateinit var mediaController: MediaControllerCompat

    /**
     * Subscribe to receive the list of media items that the service is managing
     */
    suspend fun subscribe(parentId: String = "/"): List<MediaBrowserCompat.MediaItem> {
        return mediaBrowser.subscribe(parentId)
    }

     fun observeCurrentlyPlaying(): Flow<List<MediaBrowserCompat.MediaItem>> {
        return mediaBrowser.observe(RECENT_SONG)
    }

    /**
     * Call this method once the client is done using this data
     */
    fun unsubscribe(parentId: String = "/") {
        mediaBrowser.unsubscribe(parentId)
    }

    /**
     * Callbacks used for performing the handshake with the music service and receiving
     * service status updates
     */
    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            // Initialize a Media Controller for the Media session
            mediaController = MediaControllerCompat(
                context,
                mediaBrowser.sessionToken
            )
            // Register a controller callback
            mediaController.registerCallback(controllerCallback)
        }

        /**
         * The Service has crashed. Disable transport controls until it automatically reconnects
         */
        override fun onConnectionSuspended() {
            Timber.d("Disconnected from music service")
        }

        /**
         * The Service has refused our connection
         */
        override fun onConnectionFailed() {
            Timber.d("Music service refused connection")
        }

    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.value = state ?: EMPTY_PLAYBACK_STATE
        }

        /**
         * Normally if a [MediaBrowserServiceCompat] drops its connection, the callback comes via
         * [MediaControllerCompat.Callback] (here). But since other connection status events are
         * sent to [MediaBrowserCompat.ConnectionCallback], we catch the disconnect here and send
         * it on to the other callback
         */
        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            connectionCallback.onConnectionSuspended()
        }
    }

    companion object {
        val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build()
        const val SONG_ID = MusicPlaybackService.SONG_ID
        const val RECENT_SONG = MusicPlaybackService.RECENT_SONG
    }
}