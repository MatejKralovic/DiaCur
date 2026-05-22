package me.matejkralovic.diacur.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.matejkralovic.diacur.DiaCurApp
import me.matejkralovic.diacur.R

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = context.applicationContext as DiaCurApp
        val now = System.currentTimeMillis()

        // Check reminders triggered by date
        val dateReminders = app.reminderRepository.getTriggeredByDate(now)
        dateReminders.forEach { reminder ->
            NotificationHelper.sendNotification(
                context = context,
                id = reminder.id.toInt(),
                channelId = NotificationHelper.CHANNEL_REMINDERS,
                title = context.getString(R.string.notif_reminder_title),
                message = reminder.description
            )
            app.reminderRepository.markAsCompleted(reminder.id)
        }

        // Check reminders triggered by odometer
        val kmReminders = app.reminderRepository.getTriggeredByOdometer()
        kmReminders.forEach { reminder ->
            NotificationHelper.sendNotification(
                context = context,
                id = reminder.id.toInt(),
                channelId = NotificationHelper.CHANNEL_REMINDERS,
                title = context.getString(R.string.notif_reminder_title),
                message = reminder.description
            )
            app.reminderRepository.markAsCompleted(reminder.id)
        }

        // Check inspections due for notification
        val inspections = app.inspectionRepository.getDueForNotification(now)
        inspections.forEach { inspection ->
            NotificationHelper.sendNotification(
                context = context,
                id = inspection.id.toInt() + 10000, // offset to avoid id collision with reminders
                channelId = NotificationHelper.CHANNEL_INSPECTIONS,
                title = context.getString(R.string.notif_inspection_title),
                message = context.getString(
                    R.string.notif_inspection_message,
                    context.getString(inspection.type.labelRes)
                )
            )

            val newDate = inspection.notificationDate?.plus(86400000);
            app.inspectionRepository.update(
                inspection.copy(notificationDate = newDate)
            )
        }

        return Result.success()
    }
}
// Vytvorene pomocou AI
// Chyby:
// Notifikacia o kontrole by pri povodnom spracovani prichadzala opakovane kazdych 15 minut
