package com.naveed.mymusicapp.features.common.domain.usecases

import com.naveed.mymusicapp.server.MusicServiceConnection

/**
 * Unsubscribe the current client from the media browser service
 */
class UnsubscribeMediaBrowser(
    private val musicServiceConnection: MusicServiceConnection
) {

    operator fun invoke(parentId: String = "/") {
        musicServiceConnection.unsubscribe(parentId = parentId)
    }
}