package com.naveed.mymusicapp.server.domain.usecases

import com.naveed.mymusicapp.server.MusicServiceConnection

/**
 * Unsubscribe the current client from the media brawser service
 */
class UnsubscribeMediaBrowser(
    private val musicServiceConnection: MusicServiceConnection
) {

    operator fun invoke() {
        musicServiceConnection.unsubscribe()
    }
}