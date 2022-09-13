package com.naveed.mymusicapp.server

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.naveed.mymusicapp.di.IoDispatcher
import com.naveed.mymusicapp.di.MainDispatcher
import com.naveed.mymusicapp.server.domain.MusicServiceUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlaybackService : MediaBrowserServiceCompat() {

    @Inject
    internal lateinit var musicServiceUseCases: MusicServiceUseCases

    @Inject
    @MainDispatcher
    internal lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @IoDispatcher
    internal lateinit var ioDispatcher: CoroutineDispatcher

    private lateinit var mediaSession: MediaSessionCompat


    private val serviceJob = SupervisorJob()
    private val serviceScope by lazy { CoroutineScope(mainDispatcher + serviceJob) }

    /**
     * Media player instance for playing the actual song
     */
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    override fun onCreate() {
        super.onCreate()
        setupSession()
    }

    /**
     * Returns the "root" media ID that the client should request to get the list of
     * [MediaBrowserCompat.MediaItem]s to browse
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
     * Returns (via the [result] parameter) a list of [MediaBrowserCompat.MediaItem]s that are child
     * items of the provided [parentId]
     */
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val job = serviceScope.launch(ioDispatcher) {
            musicServiceUseCases.getMediaItems()
                .onSuccess { children ->
                    result.sendResult(children.toMutableList())
                }
                .onFailure {
                    result.sendResult(null)
                }
        }
        // If the results are not ready, the service must "detach" the results before
        // the method returns. After the source is ready, the lambda above will run,
        // and the caller will be notified that the results are ready.
        if (job.isActive) {
            result.detach()
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
        mediaSession = MediaSessionCompat(this, TAG).apply {
            setCallback(MyMediaSessionCallback())
        }
        // Set the session's token so that client activities can communicate with it.
        sessionToken = mediaSession.sessionToken
    }

    private inner class MyMediaSessionCallback : MediaSessionCompat.Callback() {

        override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
            Timber.i("///", "Called play from uri: $uri")
            mediaPlayer.setDataSource(uri.toString())
            onPrepare()
            onPlay()
        }

        override fun onPause() {
            mediaPlayer.pause()
        }

        override fun onPrepare() {
            mediaPlayer.prepare()
        }

        override fun onPlay() {
            mediaPlayer.start()
        }
    }

    companion object {
        private const val TAG = "MusicService"
        private const val MEDIA_ROOT_ID = "media_root_id"
        private const val EMPTY_MEDIA_ROOT_ID = "empty_root_id"
    }
}