// ============================================================
// NotificationHelper.kt
// ============================================================
package me.matejkralovic.diacur.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import me.matejkralovic.diacur.R

object NotificationHelper {

    const val CHANNEL_REMINDERS = "diacur_reminders"
    const val CHANNEL_INSPECTIONS = "diacur_inspections"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.notif_channel_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notif_channel_reminders_desc)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_INSPECTIONS,
                context.getString(R.string.notif_channel_inspections),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notif_channel_inspections_desc)
            }
        )
    }

    fun sendNotification(
        context: Context,
        id: Int,
        channelId: String,
        title: String,
        message: String
    ) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(id, notification)
    }
}
// Vytvorene pomocou AI
