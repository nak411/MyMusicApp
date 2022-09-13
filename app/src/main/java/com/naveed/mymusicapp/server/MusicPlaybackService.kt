package com.naveed.mymusicapp.server

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.naveed.mymusicapp.di.MainDispatcher
import com.naveed.mymusicapp.server.domain.MusicServiceUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlaybackService : MediaBrowserServiceCompat() {

    @Inject
    @MainDispatcher
    internal lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    internal lateinit var musicServiceUseCases: MusicServiceUseCases

    private lateinit var mediaSession: MediaSessionCompat


    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(mainDispatcher + serviceJob)

    override fun onCreate() {
        super.onCreate()
        setupSession()
    }

    /**
     * Returns the "root" media ID that the client should request to get the list of
     * [MediaItems] to browse
     */
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        val browserRootPath = "/"
        val rootExtras = Bundle()
        return BrowserRoot(browserRootPath, rootExtras)
    }

    /**
     * Returns (via the [result] parameter) a list of [MediaItem]s that are child
     * items of the provided [parentId]
     */
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        serviceScope.launch {
            musicServiceUseCases.getMediaItems()
                .onSuccess { children ->
                    result.sendResult(children.toMutableList())
                }
                .onFailure {
                    result.sendResult(null)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clear session
        mediaSession.run {
            isActive = false
            release()
        }
        // Cancel coroutines when the service is going away
        serviceJob.cancel()
    }

    private fun setupSession() {
        // Create a session and set the token
        val session = MediaSessionCompat(this, TAG)
        sessionToken = session.sessionToken
        mediaSession = session
    }

    companion object {
        private const val TAG = "MusicService"
        private const val MEDIA_ROOT_ID = "media_root_id"
        private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    }
}