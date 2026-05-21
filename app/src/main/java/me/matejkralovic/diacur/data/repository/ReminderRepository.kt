package me.matejkralovic.diacur.data.repository

import me.matejkralovic.diacur.data.dao.ReminderDao
import me.matejkralovic.diacur.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {

    fun getActiveForVehicle(vehicleId: Long): Flow<List<Reminder>> =
        dao.getActiveForVehicle(vehicleId)

    suspend fun getById(id: Long): Reminder? = dao.getById(id)

    suspend fun getTriggeredByOdometer(): List<Reminder> = dao.getTriggeredByOdometer()

    suspend fun getTriggeredByDate(nowMillis: Long): List<Reminder> =
        dao.getTriggeredByDate(nowMillis)

    suspend fun insert(reminder: Reminder): Long = dao.insert(reminder)

    suspend fun update(reminder: Reminder) = dao.update(reminder)

    suspend fun delete(reminder: Reminder) = dao.delete(reminder)

    suspend fun markAsCompleted(id: Long) = dao.markAsCompleted(id)
}

// Vytvorene pomocou AI
