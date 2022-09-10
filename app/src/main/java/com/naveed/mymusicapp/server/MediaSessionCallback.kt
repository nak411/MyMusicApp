package com.naveed.mymusicapp.server

import android.app.Service
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.naveed.mymusicapp.R
import timber.log.Timber

class MediaSessionCallback(
    private val mediaSession: MediaSessionCompat,
    private val service: Service
): MediaSessionCompat.Callback() {

    companion object {
        private const val channelId = "com.naveed.music.NOW_PLAYING"
        private const val notificationId = 0x123
    }

    override fun onPlay() {
        super.onPlay()
        showNotification()
    }

    private fun showNotification() {
        // Get the session's metadata
        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata

        if (mediaMetadata != null) {
            val description = mediaMetadata.description

            val builder = NotificationCompat.Builder(service, channelId).apply {
                // Add the metadata for the currently playing track
                setContentTitle(description.title)
                setContentText(description.subtitle)
                setSubText(description.description)
                setLargeIcon(description.iconBitmap)

                // Enable launching the player by clicking notification
                setContentIntent(controller.sessionActivity)

                // Stop the service when notification is swiped away
                setDeleteIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        service,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )

                // Make the transport control visible on lockscreen
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                // Add an app icon and set its accent color
                setSmallIcon(androidx.core.R.drawable.notification_template_icon_bg)
                color = ContextCompat.getColor(service, androidx.appcompat.R.color.primary_dark_material_dark)

                // Add a pause button
                addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24,
                        service.getString(R.string.pause),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            service,
                            PlaybackStateCompat.ACTION_PLAY_PAUSE
                        )
                    )
                )
            }

            // Display the notification and place the service in the foreground
            service.startForeground(notificationId, builder.build())
        } else {
            Timber.e("Unable to create notification. No metadata available")
        }
    }
}