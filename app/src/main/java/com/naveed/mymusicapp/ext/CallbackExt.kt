package com.naveed.mymusicapp.ext

import android.support.v4.media.MediaBrowserCompat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Converts the callback based subscription to a coroutine
 */
suspend fun MediaBrowserCompat.subscribe(parentId: String): List<MediaBrowserCompat.MediaItem> {
    return suspendCoroutine { continuation ->
        subscribe(parentId, object : MediaBrowserCompat.SubscriptionCallback() {

            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                continuation.resume(children)
            }

            override fun onError(parentId: String) {
                continuation.resumeWithException("Unable to load children for parentId: $parentId".toException())
            }
        })
    }
}