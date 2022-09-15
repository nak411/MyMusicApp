package com.naveed.mymusicapp.ext

import android.support.v4.media.MediaBrowserCompat
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun MediaBrowserCompat.observe(parentId: String): Flow<List<MediaBrowserCompat.MediaItem>> {
    return callbackFlow {
        val callback = object : MediaBrowserCompat.SubscriptionCallback() {

            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
//                Timber.d("/// sending children")
                trySend(children)
                    .onFailure {
                        Timber.e(it)
                    }
                    .onClosed {
                        Timber.d("/// Channel closed")
                    }
            }

            override fun onError(parentId: String) {
                Timber.d("/// Canceling flow")
                cancel("Unable to load children")
            }
        }
        subscribe(parentId, callback)

        awaitClose { unsubscribe(parentId) }
    }
}

/**
 * Converts the callback based subscription to a coroutine
 */
suspend fun MediaBrowserCompat.subscribe(parentId: String): List<MediaBrowserCompat.MediaItem> {
    return suspendCancellableCoroutine { continuation ->
        val callback = object : MediaBrowserCompat.SubscriptionCallback() {

            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                // Resume coroutine with a value provided by the callback
                continuation.resume(children)
            }

            override fun onError(parentId: String) {
                // Resume coroutine with an exception provided by the callback
                continuation.resumeWithException("Unable to load children for parentId: $parentId".toException())
            }
        }
        // Register callback with an API
        subscribe(parentId, callback)
        // Remove callback on cancellation
        continuation.invokeOnCancellation {
            Timber.d("/// Unsubscribed")
            unsubscribe(parentId, callback)
        }

    }
}