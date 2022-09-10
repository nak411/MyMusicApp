package com.naveed.mymusicapp.server

import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.service.media.MediaBrowserService

class MediaPlaybackService : MediaBrowserService() {

    private var mediaSession: MediaSession? = null
    private lateinit var stateBuilder: PlaybackState.Builder

    companion object {
        private const val TAG = "MusicService"
        private const val MEDIA_ROOT_ID = "media_root_id"
        private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    }

    override fun onCreate() {
        super.onCreate()
        

        // Create a media session
        mediaSession = MediaSession(baseContext, TAG).apply {
            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE)
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(MediaSessionCallback())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        // Clients can connect, but this BrowserRoot is an empty hierachy
        // so onLoadChildren returns nothing. This disables the ability to browse for content.
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        // Browsing not allowed
        if (EMPTY_MEDIA_ROOT_ID == parentMediaId) {
            result.sendResult(null)
            return
        }

        val mediaItems = mutableListOf<MediaBrowser.MediaItem>()
        // Check if this is the root menu:
        if (MEDIA_ROOT_ID == parentMediaId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list
        }
        result.sendResult(mediaItems)
    }
}