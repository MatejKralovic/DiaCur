package me.matejkralovic.diacur.notifications

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    private const val WORK_NAME = "diacur_reminder_check"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // don't replace if already scheduled
            request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
// Vytvorene pomocou AI
