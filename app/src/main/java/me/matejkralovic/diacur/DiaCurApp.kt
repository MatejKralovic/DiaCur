package me.matejkralovic.diacur

import android.app.Application
import me.matejkralovic.diacur.data.db.DiaCurDatabase
import me.matejkralovic.diacur.data.repository.*
import me.matejkralovic.diacur.notifications.NotificationHelper
import me.matejkralovic.diacur.notifications.NotificationScheduler

class DiaCurApp : Application() {

    val database by lazy { DiaCurDatabase.getDatabase(this) }

    val vehicleRepository by lazy { VehicleRepository(database.vehicleDao()) }
    val fuelingRepository by lazy { FuelingRepository(database.fuelingDao()) }
    val serviceRepository by lazy { ServiceRepository(database.serviceDao()) }
    val inspectionRepository by lazy { InspectionRepository(database.inspectionDao()) }
    val reminderRepository by lazy { ReminderRepository(database.reminderDao()) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        NotificationScheduler.schedule(this)
    }
}
// Vytvorene pomocou AI
