package com.naveed.mymusicapp.server

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat

class MusicPlaybackService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null

    override fun onCreate() {
        super.onCreate()
        setupSession()
    }

    private fun setupSession() {
        // Create a session and set the token
        val session  = MediaSessionCompat(this, TAG)
        sessionToken = session.sessionToken
        mediaSession = session
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        // Clients can connect, but this BrowserRoot is an empty hierarchy
        // so onLoadChildren returns nothing. This disables the ability to browse for content.
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }

    companion object {
        private const val TAG = "MusicService"
        private const val MEDIA_ROOT_ID = "media_root_id"
        private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    }
}